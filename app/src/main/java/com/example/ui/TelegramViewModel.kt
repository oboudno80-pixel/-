package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.TelegramDatabase
import com.example.data.model.AiModelType
import com.example.data.model.ChatEntity
import com.example.data.model.ChatType
import com.example.data.model.MessageEntity
import com.example.data.model.PhonePrivacyLevel
import com.example.data.model.UserProfile
import com.example.data.repository.ChatRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ChatCategory(val title: String, val iconEmoji: String) {
    ALL("Все", "💬"),
    AI_BOTS("ИИ Боты", "🤖"),
    DIRECT("Личные", "👤"),
    CHANNELS("Каналы", "📢"),
    SAVED("Избранное", "🔖")
}

@OptIn(ExperimentalCoroutinesApi::class)
class TelegramViewModel(application: Application) : AndroidViewModel(application) {
    private val database = TelegramDatabase.getDatabase(application)
    private val repository = ChatRepository(
        chatDao = database.chatDao(),
        messageDao = database.messageDao(),
        context = application
    )

    val userProfile: StateFlow<UserProfile> = repository.userProfile

    private val _selectedChatId = MutableStateFlow<String?>(null)
    val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ChatCategory.ALL)
    val selectedCategory: StateFlow<ChatCategory> = _selectedCategory.asStateFlow()

    private val _isGeneratingResponse = MutableStateFlow(false)
    val isGeneratingResponse: StateFlow<Boolean> = _isGeneratingResponse.asStateFlow()

    private val _replyingMessage = MutableStateFlow<MessageEntity?>(null)
    val replyingMessage: StateFlow<MessageEntity?> = _replyingMessage.asStateFlow()

    private val _isRecordingAudio = MutableStateFlow(false)
    val isRecordingAudio: StateFlow<Boolean> = _isRecordingAudio.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private var recordingJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    // Filtered chats flow by category and search query (name, username @xxx, phone, message)
    val chats: StateFlow<List<ChatEntity>> = combine(
        repository.allChats,
        _selectedCategory,
        _searchQuery
    ) { allChats, category, query ->
        allChats.filter { chat ->
            val matchesCategory = when (category) {
                ChatCategory.ALL -> true
                ChatCategory.AI_BOTS -> chat.chatType == ChatType.AI_BOT
                ChatCategory.DIRECT -> chat.chatType == ChatType.DIRECT
                ChatCategory.CHANNELS -> chat.chatType == ChatType.CHANNEL
                ChatCategory.SAVED -> chat.id == "saved_messages"
            }
            val cleanQuery = query.trim().removePrefix("@")
            val matchesQuery = if (cleanQuery.isBlank()) {
                true
            } else {
                chat.title.contains(cleanQuery, ignoreCase = true) ||
                        chat.username.contains(cleanQuery, ignoreCase = true) ||
                        chat.phoneNumber.contains(cleanQuery, ignoreCase = true) ||
                        chat.lastMessageText.contains(cleanQuery, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current selected chat entity
    val currentChat: StateFlow<ChatEntity?> = _selectedChatId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getChat(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Current chat messages
    val currentMessages: StateFlow<List<MessageEntity>> = _selectedChatId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getMessages(id)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectChat(chatId: String?) {
        _selectedChatId.value = chatId
        _replyingMessage.value = null
        if (chatId != null) {
            viewModelScope.launch {
                repository.clearUnread(chatId)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: ChatCategory) {
        _selectedCategory.value = category
    }

    fun selectCategory(category: ChatCategory) {
        _selectedCategory.value = category
    }

    fun setReplyingMessage(message: MessageEntity?) {
        _replyingMessage.value = message
    }

    fun sendMessage(text: String) {
        val chatId = _selectedChatId.value ?: return
        if (text.isBlank()) return

        val reply = _replyingMessage.value
        _replyingMessage.value = null

        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = text.trim(),
                replyToText = reply?.text?.take(40),
                replyToSender = reply?.sender,
                onGeneratingChanged = { isGen ->
                    _isGeneratingResponse.value = isGen
                }
            )
        }
    }

    fun sendVoiceMessage(durationSec: Int) {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.sendMessage(
                chatId = chatId,
                text = "🎤 Голосовое сообщение",
                isVoice = true,
                voiceDurationSec = durationSec,
                onGeneratingChanged = { isGen ->
                    _isGeneratingResponse.value = isGen
                }
            )
        }
    }

    fun startAudioRecording() {
        _isRecordingAudio.value = true
        _recordingSeconds.value = 0
        recordingJob?.cancel()
        recordingJob = viewModelScope.launch {
            while (_isRecordingAudio.value) {
                delay(1000)
                _recordingSeconds.value += 1
            }
        }
    }

    fun stopAudioRecording(send: Boolean) {
        val duration = _recordingSeconds.value
        _isRecordingAudio.value = false
        recordingJob?.cancel()
        recordingJob = null
        if (send && duration > 0) {
            sendVoiceMessage(duration)
        }
        _recordingSeconds.value = 0
    }

    fun setModel(model: AiModelType) {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.updateChatModel(chatId, model)
        }
    }

    fun updateSettings(systemPrompt: String, temperature: Float, customApiKey: String) {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.updateChatSettings(chatId, systemPrompt, temperature, customApiKey)
        }
    }

    fun toggleReaction(messageId: String, currentReaction: String?, newReaction: String) {
        val next = if (currentReaction == newReaction) null else newReaction
        viewModelScope.launch {
            repository.updateReaction(messageId, next)
        }
    }

    fun clearChatHistory() {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.clearChatMessages(chatId)
        }
    }

    fun deleteCurrentChat() {
        val chatId = _selectedChatId.value ?: return
        viewModelScope.launch {
            repository.deleteChat(chatId)
            _selectedChatId.value = null
        }
    }

    fun createChat(
        title: String,
        type: ChatType,
        avatarEmoji: String,
        username: String = "",
        phoneNumber: String = "",
        model: AiModelType = AiModelType.GEMINI_3_5_FLASH
    ) {
        viewModelScope.launch {
            val newId = repository.createNewChat(title, type, avatarEmoji, username, phoneNumber, model)
            _selectedChatId.value = newId
        }
    }

    // User Profile & Registration Management
    fun completeRegistration(
        phone: String,
        isPhoneHidden: Boolean,
        privacyLevel: PhonePrivacyLevel,
        firstName: String,
        lastName: String,
        username: String,
        bio: String,
        avatarEmoji: String,
        avatarColor: Long
    ) {
        val profile = UserProfile(
            phoneNumber = phone,
            isPhoneHidden = isPhoneHidden,
            phonePrivacyLevel = privacyLevel,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            username = username.trim().removePrefix("@"),
            bio = bio.trim(),
            avatarEmoji = avatarEmoji,
            avatarColorHex = avatarColor,
            isRegistered = true
        )
        repository.saveUserProfile(profile)
    }

    fun updatePhonePrivacy(isPhoneHidden: Boolean, privacyLevel: PhonePrivacyLevel) {
        val current = userProfile.value
        val updated = current.copy(
            isPhoneHidden = isPhoneHidden,
            phonePrivacyLevel = privacyLevel
        )
        repository.saveUserProfile(updated)
    }

    fun updateProfile(firstName: String, lastName: String, username: String, bio: String) {
        val current = userProfile.value
        val updated = current.copy(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            username = username.trim().removePrefix("@"),
            bio = bio.trim()
        )
        repository.saveUserProfile(updated)
    }

    fun logout() {
        val current = userProfile.value
        val loggedOut = current.copy(isRegistered = false)
        repository.saveUserProfile(loggedOut)
        _selectedChatId.value = null
    }
}
