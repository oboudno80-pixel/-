package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ChatType {
    AI_BOT,
    DIRECT,
    CHANNEL,
    GROUP
}

enum class AiModelType(
    val id: String,
    val displayName: String,
    val provider: String,
    val iconEmoji: String,
    val apiModelName: String,
    val description: String,
    val category: AiCategory = AiCategory.TEXT
) {
    GEMINI_3_5_FLASH(
        id = "gemini_3_5_flash",
        displayName = "Gemini 3.5 Flash",
        provider = "Google AI",
        iconEmoji = "⚡",
        apiModelName = "gemini-3.5-flash",
        description = "Сверхбыстрая мультимодальная модель от Google с высокой точностью и мгновенным ответом.",
        category = AiCategory.TEXT
    ),
    GEMINI_3_1_PRO(
        id = "gemini_3_1_pro",
        displayName = "Gemini 3.1 Pro",
        provider = "Google AI",
        iconEmoji = "🧠",
        apiModelName = "gemini-3.1-pro-preview",
        description = "Мощная флагманская модель Google для сложного логического мышления, кода и глубокого анализа.",
        category = AiCategory.TEXT
    ),
    CHATGPT_4O(
        id = "chatgpt_4o",
        displayName = "ChatGPT-4o (Omni)",
        provider = "OpenAI",
        iconEmoji = "🌌",
        apiModelName = "gpt-4o",
        description = "Флагманская модель OpenAI Omni: глубокое понимание контекста, креативность и живой диалог.",
        category = AiCategory.TEXT
    ),
    CHATGPT_4O_MINI(
        id = "chatgpt_4o_mini",
        displayName = "ChatGPT-4o Mini",
        provider = "OpenAI",
        iconEmoji = "🚀",
        apiModelName = "gpt-4o-mini",
        description = "Быстрая и эффективная версия GPT-4o для повседневных задач и коротких ответов.",
        category = AiCategory.TEXT
    ),
    IMAGEN_3_PHOTO(
        id = "imagen_3_photo",
        displayName = "Imagen 3 • Фотостудия",
        provider = "Google AI & DALL-E",
        iconEmoji = "🎨",
        apiModelName = "imagen-3.0-generate-002",
        description = "Генерация фотореалистичных 4K изображений, цифровых артов, концептов и аватарок по промпту.",
        category = AiCategory.IMAGE
    ),
    VEO_VIDEO_STUDIO(
        id = "veo_video_studio",
        displayName = "Veo & Sora • Видеостудия",
        provider = "DeepMind & OpenAI",
        iconEmoji = "🎬",
        apiModelName = "veo-2.0-generate",
        description = "Генерация кинематографичных видеороликов, синемаграфий и динамических сцен с контролем камеры.",
        category = AiCategory.VIDEO
    ),
    OMNI_ASSISTANT(
        id = "omni_assistant",
        displayName = "Omni Universal AI",
        provider = "Hybrid AI",
        iconEmoji = "✨",
        apiModelName = "omni-hybrid",
        description = "Универсальный ИИ-ассистент Telegram с авто-подбором оптимального режима генерации.",
        category = AiCategory.ALL
    );

    companion object {
        fun fromId(id: String?): AiModelType {
            return entries.find { it.id == id } ?: GEMINI_3_5_FLASH
        }
    }
}

enum class AiCategory {
    TEXT,
    IMAGE,
    VIDEO,
    ALL
}

enum class MessageSender {
    ME,
    BOT,
    OTHER
}

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

enum class MediaType {
    NONE,
    IMAGE,
    VIDEO
}

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val avatarEmoji: String = "💬",
    val avatarColorHex: Long = 0xFF2AABEE,
    val isPinned: Boolean = false,
    val isVerified: Boolean = false,
    val unreadCount: Int = 0,
    val type: String = ChatType.DIRECT.name,
    val selectedModelId: String = AiModelType.GEMINI_3_5_FLASH.id,
    val systemPrompt: String = "Ты умный, дружелюбный и эрудированный ИИ-помощник в Telegram мессенджере.",
    val temperature: Float = 0.7f,
    val customApiKey: String = "",
    val lastMessageText: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val isOnline: Boolean = true
) {
    val chatType: ChatType
        get() = try {
            ChatType.valueOf(type)
        } catch (e: Exception) {
            ChatType.DIRECT
        }

    val activeModel: AiModelType
        get() = AiModelType.fromId(selectedModelId)

    val displayUsername: String
        get() = if (username.isNotBlank()) {
            if (username.startsWith("@")) username else "@$username"
        } else ""
}

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val chatId: String,
    val text: String,
    val sender: String = MessageSender.ME.name,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = MessageStatus.READ.name,
    val isVoice: Boolean = false,
    val voiceDurationSec: Int = 0,
    val replyToText: String? = null,
    val replyToSender: String? = null,
    val reaction: String? = null,
    val modelBadge: String? = null,
    // Media generation fields
    val mediaType: String = MediaType.NONE.name,
    val mediaPrompt: String? = null,
    val mediaStyle: String? = null,
    val mediaSeed: Int = 1,
    val videoDurationSec: Int = 5,
    val mediaAspectRatio: String = "16:9"
) {
    val messageSender: MessageSender
        get() = try {
            MessageSender.valueOf(sender)
        } catch (e: Exception) {
            MessageSender.ME
        }

    val messageStatus: MessageStatus
        get() = try {
            MessageStatus.valueOf(status)
        } catch (e: Exception) {
            MessageStatus.READ
        }

    val activeMediaType: MediaType
        get() = try {
            MediaType.valueOf(mediaType)
        } catch (e: Exception) {
            MediaType.NONE
        }
}
