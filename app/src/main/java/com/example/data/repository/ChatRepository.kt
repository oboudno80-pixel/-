package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.ai.AiService
import com.example.data.db.ChatDao
import com.example.data.db.MessageDao
import com.example.data.model.AiModelType
import com.example.data.model.ChatEntity
import com.example.data.model.ChatType
import com.example.data.model.MediaType
import com.example.data.model.MessageEntity
import com.example.data.model.MessageSender
import com.example.data.model.MessageStatus
import com.example.data.model.PhonePrivacyLevel
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class ChatRepository(
    private val chatDao: ChatDao,
    private val messageDao: MessageDao,
    private val context: Context? = null,
    private val aiService: AiService = AiService()
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val prefs: SharedPreferences? = context?.getSharedPreferences("tg_user_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    val allChats: Flow<List<ChatEntity>> = chatDao.getAllChats()

    fun getMessages(chatId: String): Flow<List<MessageEntity>> = messageDao.getMessagesForChat(chatId)

    fun getChat(chatId: String): Flow<ChatEntity?> = chatDao.getChatById(chatId)

    private fun loadUserProfile(): UserProfile {
        if (prefs == null) {
            return UserProfile(isRegistered = true)
        }
        val isRegistered = prefs.getBoolean("is_registered", true)
        val phone = prefs.getString("phone_number", "+7 999 123-45-67") ?: "+7 999 123-45-67"
        val isHidden = prefs.getBoolean("is_phone_hidden", true)
        val privacyStr = prefs.getString("privacy_level", PhonePrivacyLevel.NOBODY.name) ?: PhonePrivacyLevel.NOBODY.name
        val privacy = try { PhonePrivacyLevel.valueOf(privacyStr) } catch (e: Exception) { PhonePrivacyLevel.NOBODY }
        val firstName = prefs.getString("first_name", "Павел") ?: "Павел"
        val lastName = prefs.getString("last_name", "Дуров") ?: "Дуров"
        val username = prefs.getString("username", "pavel_tg") ?: "pavel_tg"
        val bio = prefs.getString("bio", "Telegram Liquid Glass • ИИ Фото & Видео ✨") ?: ""
        val emoji = prefs.getString("avatar_emoji", "⚡") ?: "⚡"
        val colorHex = prefs.getLong("avatar_color", 0xFF24A1DE)

        return UserProfile(
            phoneNumber = phone,
            isPhoneHidden = isHidden,
            phonePrivacyLevel = privacy,
            firstName = firstName,
            lastName = lastName,
            username = username,
            bio = bio,
            avatarEmoji = emoji,
            avatarColorHex = colorHex,
            isRegistered = isRegistered
        )
    }

    fun saveUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        prefs?.edit()?.apply {
            putBoolean("is_registered", profile.isRegistered)
            putString("phone_number", profile.phoneNumber)
            putBoolean("is_phone_hidden", profile.isPhoneHidden)
            putString("privacy_level", profile.phonePrivacyLevel.name)
            putString("first_name", profile.firstName)
            putString("last_name", profile.lastName)
            putString("username", profile.username)
            putString("bio", profile.bio)
            putString("avatar_emoji", profile.avatarEmoji)
            putLong("avatar_color", profile.avatarColorHex)
            apply()
        }
    }

    suspend fun initializeSeedDataIfNeeded() {
        val count = chatDao.getChatCount()
        if (count == 0) {
            val now = System.currentTimeMillis()

            // 1. Text AI Bot
            val aiTextChat = ChatEntity(
                id = "ai_assistant",
                title = "ИИ Помощник (Gemini & ChatGPT)",
                subtitle = "бот • всегда онлайн",
                username = "ai_assistant_bot",
                phoneNumber = "",
                avatarEmoji = "🤖",
                avatarColorHex = 0xFF7C4DFF,
                isPinned = true,
                isVerified = true,
                unreadCount = 1,
                type = ChatType.AI_BOT.name,
                selectedModelId = AiModelType.GEMINI_3_5_FLASH.id,
                systemPrompt = "Ты умный, полезный и дружелюбный ИИ-помощник в Telegram Liquid Glass. Отвечай подробно, понятно и поддерживай живую беседу с эмодзи.",
                temperature = 0.7f,
                lastMessageText = "⚡ Готов к работе! Поддерживаю Gemini, ChatGPT, код и тексты.",
                lastMessageTime = now,
                isOnline = true
            )

            // 2. Photo Generation AI Bot (Imagen 3 / DALL-E)
            val aiPhotoChat = ChatEntity(
                id = "ai_photo_studio",
                title = "ИИ Фотостудия (Imagen 3)",
                subtitle = "генерация фото & арт",
                username = "imagen3_bot",
                phoneNumber = "",
                avatarEmoji = "🎨",
                avatarColorHex = 0xFFD500F9,
                isPinned = true,
                isVerified = true,
                unreadCount = 1,
                type = ChatType.AI_BOT.name,
                selectedModelId = AiModelType.IMAGEN_3_PHOTO.id,
                systemPrompt = "Ты специализированный ИИ-генератор фото и изображений Imagen 3 и DALL-E.",
                temperature = 0.8f,
                lastMessageText = "🎨 Напиши, что нарисовать, и я сгенерирую реалистичное фото 4K!",
                lastMessageTime = now - 10000,
                isOnline = true
            )

            // 3. Video Generation AI Bot (Veo / Sora)
            val aiVideoChat = ChatEntity(
                id = "ai_video_studio",
                title = "ИИ Видеостудия (Veo & Sora)",
                subtitle = "генерация видео 60fps",
                username = "veo_video_bot",
                phoneNumber = "",
                avatarEmoji = "🎬",
                avatarColorHex = 0xFF00E5FF,
                isPinned = true,
                isVerified = true,
                unreadCount = 1,
                type = ChatType.AI_BOT.name,
                selectedModelId = AiModelType.VEO_VIDEO_STUDIO.id,
                systemPrompt = "Ты специализированный ИИ-генератор кинематографичного видео Veo и Sora.",
                temperature = 0.8f,
                lastMessageText = "🎬 Опиши сцену, и я создам динамический видеоклип!",
                lastMessageTime = now - 20000,
                isOnline = true
            )

            // 4. Pavel Durov
            val durovChat = ChatEntity(
                id = "durov",
                title = "Павел Дуров",
                subtitle = "был(а) только что",
                username = "durov",
                phoneNumber = "+7 999 777-00-01",
                avatarEmoji = "👑",
                avatarColorHex = 0xFF00B4D8,
                isPinned = false,
                isVerified = true,
                unreadCount = 0,
                type = ChatType.DIRECT.name,
                lastMessageText = "Liquid Glass интерфейс в Telegram выглядит великолепно. Будущее уже здесь!",
                lastMessageTime = now - 3600000,
                isOnline = false
            )

            // 5. Telegram News
            val tgNewsChat = ChatEntity(
                id = "tg_news",
                title = "Telegram News",
                subtitle = "9.5M подписчиков",
                username = "telegram",
                phoneNumber = "",
                avatarEmoji = "✈️",
                avatarColorHex = 0xFF0088CC,
                isPinned = false,
                isVerified = true,
                unreadCount = 2,
                type = ChatType.CHANNEL.name,
                lastMessageText = "🔥 Революционное обновление: стиль Liquid Glass, регистрация с приватностью номера и ИИ Фото/Видео студии!",
                lastMessageTime = now - 7200000,
                isOnline = false
            )

            // 6. Alex Designer
            val alexChat = ChatEntity(
                id = "alex_design",
                title = "Алексей Смирнов",
                subtitle = "онлайн",
                username = "alex_design",
                phoneNumber = "+7 916 555-32-11",
                avatarEmoji = "💎",
                avatarColorHex = 0xFF00E676,
                isPinned = false,
                isVerified = false,
                unreadCount = 0,
                type = ChatType.DIRECT.name,
                lastMessageText = "Привет! Зацени этот потрясающий эффект жидкого стекла и полупрозрачные карточки!",
                lastMessageTime = now - 18000000,
                isOnline = true
            )

            val seedChats = listOf(aiTextChat, aiPhotoChat, aiVideoChat, durovChat, tgNewsChat, alexChat)
            chatDao.insertChats(seedChats)

            // Seed messages for AI Assistant
            val aiSeedMessages = listOf(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_assistant",
                    text = "Привет! 👋 Я персональный ИИ-ассистент Telegram Liquid Glass.\n\nПоддерживаю переключение между флагманскими моделями:\n• ⚡ **Gemini 3.5 Flash** (Google)\n• 🧠 **Gemini 3.1 Pro** (Google)\n• 🌌 **ChatGPT-4o Omni** (OpenAI)\n• 🎨 **Imagen 3** (Фотостудия)\n• 🎬 **Veo & Sora** (Видеостудия)\n\nНажми на переключатель модели вверху, чтобы сменить движок.",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 120000,
                    status = MessageStatus.READ.name,
                    modelBadge = "⚡ Gemini 3.5 Flash"
                ),
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_assistant",
                    text = "⚡ Готов к работе! Поддерживаю Gemini, ChatGPT, код и тексты.",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 60000,
                    status = MessageStatus.READ.name,
                    modelBadge = "⚡ Gemini 3.5 Flash"
                )
            )

            // Seed messages for Photo Studio Bot
            val photoSeedMessages = listOf(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_photo_studio",
                    text = "🎨 Добро пожаловать в **Imagen 3 Photo Studio**!\n\nЯ генерирую детализированные фото, цифровой арт, концепты и стикеры. Напиши любой запрос, например:\n• *«Неоновый кот в стиле киберпанк 4K»*\n• *«Футуристический спорткар на закате в стиле Liquid Glass»*",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 300000,
                    status = MessageStatus.READ.name,
                    modelBadge = "🎨 Imagen 3"
                ),
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_photo_studio",
                    text = "🎨 Изображение сгенерировано нейросетью **Imagen 3**:\n\n✨ **Промпт:** «Футуристический город в стиле Liquid Glass на закате»\n🎭 **Стиль:** Cyberpunk 3D Render 8K HDR",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 180000,
                    status = MessageStatus.READ.name,
                    modelBadge = "🎨 Imagen 3",
                    mediaType = MediaType.IMAGE.name,
                    mediaPrompt = "Футуристический город в стиле Liquid Glass на закате",
                    mediaStyle = "Cyberpunk 3D Render 8K HDR",
                    mediaSeed = 42100
                )
            )

            // Seed messages for Video Studio Bot
            val videoSeedMessages = listOf(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_video_studio",
                    text = "🎬 Привет! Я **Veo & Sora Video AI** — создаю кинематографичные видеоролики и анимации.\n\nОпиши сцену, динамику камеры и освещение, например:\n• *«Полет дрона над ночным Токио под дождем 60fps»*\n• *«Капли жидкого стекла, плавно сливающиеся в космосе»*",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 400000,
                    status = MessageStatus.READ.name,
                    modelBadge = "🎬 Veo Video 2.0"
                ),
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "ai_video_studio",
                    text = "🎬 Видеоролик создан моделью **Google Veo AI**:\n\n✨ **Сцена:** «Капли жидкого стекла и неоновые переливы в невесомости»\n📹 **Камера:** Плавный зум 60 FPS • 4K",
                    sender = MessageSender.BOT.name,
                    timestamp = now - 240000,
                    status = MessageStatus.READ.name,
                    modelBadge = "🎬 Veo Video 2.0",
                    mediaType = MediaType.VIDEO.name,
                    mediaPrompt = "Капли жидкого стекла и неоновые переливы в невесомости",
                    mediaStyle = "Cinematic Liquid Motion • 60 FPS",
                    mediaSeed = 88412,
                    videoDurationSec = 5
                )
            )

            // Seed messages for Durov
            val durovSeedMessages = listOf(
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "durov",
                    text = "Привет! Как тебе новый стиль Liquid Glass и интеграция генераторов фото/видео?",
                    sender = MessageSender.OTHER.name,
                    timestamp = now - 7200000,
                    status = MessageStatus.READ.name
                ),
                MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = "durov",
                    text = "Liquid Glass интерфейс в Telegram выглядит великолепно. Будущее уже здесь!",
                    sender = MessageSender.OTHER.name,
                    timestamp = now - 3600000,
                    status = MessageStatus.READ.name,
                    reaction = "🔥"
                )
            )

            messageDao.insertMessages(aiSeedMessages + photoSeedMessages + videoSeedMessages + durovSeedMessages)
        }
    }

    suspend fun sendMessage(
        chatId: String,
        text: String,
        replyToText: String? = null,
        replyToSender: String? = null,
        isVoice: Boolean = false,
        voiceDurationSec: Int = 0,
        onGeneratingChanged: ((Boolean) -> Unit)? = null
    ) {
        val now = System.currentTimeMillis()
        val userMessage = MessageEntity(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            text = text,
            sender = MessageSender.ME.name,
            timestamp = now,
            status = MessageStatus.SENT.name,
            isVoice = isVoice,
            voiceDurationSec = voiceDurationSec,
            replyToText = replyToText,
            replyToSender = replyToSender
        )

        messageDao.insertMessage(userMessage)
        val preview = if (isVoice) "🎤 Голосовое сообщение ($voiceDurationSec сек)" else text
        chatDao.updateLastMessage(chatId, preview, now)

        val chat = chatDao.getChatByIdSync(chatId) ?: return

        if (chat.chatType == ChatType.AI_BOT) {
            scope.launch {
                onGeneratingChanged?.invoke(true)
                delay(900) // Realistic AI synthesis delay

                try {
                    val messages = messageDao.getMessagesForChat(chatId).first()
                    val activeModel = chat.activeModel

                    val mediaResult = aiService.generateMediaOrText(
                        prompt = text,
                        model = activeModel,
                        history = messages,
                        systemPrompt = chat.systemPrompt,
                        temperature = chat.temperature,
                        customApiKey = chat.customApiKey
                    )

                    val botMessage = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = mediaResult.text,
                        sender = MessageSender.BOT.name,
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.READ.name,
                        modelBadge = "${activeModel.iconEmoji} ${activeModel.displayName}",
                        mediaType = mediaResult.mediaType.name,
                        mediaPrompt = mediaResult.mediaPrompt,
                        mediaStyle = mediaResult.mediaStyle,
                        mediaSeed = mediaResult.mediaSeed,
                        videoDurationSec = mediaResult.videoDurationSec,
                        mediaAspectRatio = mediaResult.mediaAspectRatio
                    )

                    messageDao.insertMessage(botMessage)
                    val updatePreview = if (mediaResult.mediaType == MediaType.IMAGE) "🎨 [Фотография 4K]" else if (mediaResult.mediaType == MediaType.VIDEO) "🎬 [Видеоролик 60fps]" else mediaResult.text.take(60)
                    chatDao.updateLastMessage(chatId, updatePreview, System.currentTimeMillis())
                } catch (e: Exception) {
                    val errorMessage = MessageEntity(
                        id = UUID.randomUUID().toString(),
                        chatId = chatId,
                        text = "⚠️ Ошибка генерации: ${e.localizedMessage ?: "Попробуйте позже"}",
                        sender = MessageSender.BOT.name,
                        timestamp = System.currentTimeMillis(),
                        status = MessageStatus.READ.name,
                        modelBadge = "System"
                    )
                    messageDao.insertMessage(errorMessage)
                } finally {
                    onGeneratingChanged?.invoke(false)
                }
            }
        } else if (chat.chatType == ChatType.DIRECT && chatId != "saved_messages") {
            // Simulated contact response
            scope.launch {
                onGeneratingChanged?.invoke(true)
                delay(1800)
                val reply = when (chatId) {
                    "durov" -> "Отлично! В Telegram мы всегда стремимся к максимальной скорости, безопасности и передовому дизайну Liquid Glass."
                    "alex_design" -> "Согласен на 100%! Эффект матового стекла и переливы света просто потрясающие."
                    else -> "Привет! Получил твое сообщение в Telegram Liquid Glass ✨"
                }
                val otherMsg = MessageEntity(
                    id = UUID.randomUUID().toString(),
                    chatId = chatId,
                    text = reply,
                    sender = MessageSender.OTHER.name,
                    timestamp = System.currentTimeMillis(),
                    status = MessageStatus.READ.name
                )
                messageDao.insertMessage(otherMsg)
                chatDao.updateLastMessage(chatId, reply, System.currentTimeMillis())
                onGeneratingChanged?.invoke(false)
            }
        }
    }

    suspend fun updateChatModel(chatId: String, model: AiModelType) {
        chatDao.updateChatModel(chatId, model.id)
    }

    suspend fun updateChatSettings(
        chatId: String,
        systemPrompt: String,
        temperature: Float,
        customApiKey: String
    ) {
        chatDao.updateChatSettings(chatId, systemPrompt, temperature, customApiKey)
    }

    suspend fun updateReaction(messageId: String, reaction: String?) {
        messageDao.updateReaction(messageId, reaction)
    }

    suspend fun clearChatMessages(chatId: String) {
        messageDao.clearMessagesForChat(chatId)
        chatDao.updateLastMessage(chatId, "История очищена", System.currentTimeMillis())
    }

    suspend fun deleteChat(chatId: String) {
        messageDao.clearMessagesForChat(chatId)
        chatDao.deleteChat(chatId)
    }

    suspend fun createNewChat(
        title: String,
        type: ChatType,
        avatarEmoji: String,
        username: String = "",
        phoneNumber: String = "",
        model: AiModelType = AiModelType.GEMINI_3_5_FLASH
    ): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val colorHex = when (type) {
            ChatType.AI_BOT -> 0xFF7C4DFF
            ChatType.CHANNEL -> 0xFF0088CC
            ChatType.GROUP -> 0xFF00E676
            ChatType.DIRECT -> 0xFF00B4D8
        }

        val subtitle = when (type) {
            ChatType.AI_BOT -> "ИИ Бот • ${model.displayName}"
            ChatType.CHANNEL -> "Канал • 1 подписчик"
            ChatType.GROUP -> "Группа • 2 участника"
            ChatType.DIRECT -> "был(а) недавно"
        }

        val newChat = ChatEntity(
            id = id,
            title = title,
            subtitle = subtitle,
            username = username,
            phoneNumber = phoneNumber,
            avatarEmoji = avatarEmoji,
            avatarColorHex = colorHex,
            isPinned = false,
            isVerified = type == ChatType.AI_BOT,
            unreadCount = 0,
            type = type.name,
            selectedModelId = model.id,
            lastMessageText = if (type == ChatType.AI_BOT) "Чат с ИИ создан. Напишите сообщение!" else "Чат создан",
            lastMessageTime = now,
            isOnline = type == ChatType.AI_BOT
        )

        chatDao.insertChat(newChat)
        return id
    }

    suspend fun clearUnread(chatId: String) {
        chatDao.clearUnread(chatId)
    }
}
