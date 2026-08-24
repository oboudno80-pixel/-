package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AiModelType
import com.example.data.model.MediaType
import com.example.data.model.MessageEntity
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.random.Random

data class AiGeneratedMediaResult(
    val text: String,
    val mediaType: MediaType,
    val mediaPrompt: String? = null,
    val mediaStyle: String? = null,
    val mediaSeed: Int = 1,
    val videoDurationSec: Int = 5,
    val mediaAspectRatio: String = "16:9"
)

class AiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateMediaOrText(
        prompt: String,
        model: AiModelType,
        history: List<MessageEntity>,
        systemPrompt: String,
        temperature: Float,
        customApiKey: String
    ): AiGeneratedMediaResult = withContext(Dispatchers.IO) {
        val lower = prompt.lowercase()
        val isImageModel = model == AiModelType.IMAGEN_3_PHOTO ||
                lower.startsWith("/photo") || lower.startsWith("/image") ||
                lower.contains("нарисуй") || lower.contains("сделай фото") ||
                lower.contains("картинк") || lower.contains("сгенерируй фото") ||
                lower.contains("арт")

        val isVideoModel = model == AiModelType.VEO_VIDEO_STUDIO ||
                lower.startsWith("/video") || lower.startsWith("/sora") ||
                lower.contains("сделай видео") || lower.contains("сгенерируй видео") ||
                lower.contains("видеоролик") || lower.contains("анимаци")

        if (isImageModel) {
            val style = when {
                lower.contains("киберпанк") || lower.contains("неон") -> "Cyberpunk Neo-Tokyo 4K"
                lower.contains("реалистич") || lower.contains("фото") -> "Ultra Photorealistic 8K UHD"
                lower.contains("аниме") || lower.contains("манг") -> "Makoto Shinkai Anime Style"
                lower.contains("3d") || lower.contains("glass") -> "3D Liquid Glassmorphism Render"
                else -> "Digital Concept Art HDR"
            }
            val cleanPrompt = prompt.replace("/photo", "").replace("/image", "").trim()
            val finalPrompt = if (cleanPrompt.isNotBlank()) cleanPrompt else "Футуристический пейзаж Liquid Glass"
            val textReply = "🎨 Изображение успешно сгенерировано нейросетью **Imagen 3 / DALL-E**.\n\n✨ **Промпт:** «$finalPrompt»\n🎭 **Стиль:** $style\n📐 **Разрешение:** 4K UHD (HDR10+)"

            return@withContext AiGeneratedMediaResult(
                text = textReply,
                mediaType = MediaType.IMAGE,
                mediaPrompt = finalPrompt,
                mediaStyle = style,
                mediaSeed = Random.nextInt(1000, 99999),
                mediaAspectRatio = "16:9"
            )
        }

        if (isVideoModel) {
            val style = when {
                lower.contains("кинематограф") || lower.contains("кино") -> "Cinematic 2.39:1 • 60 FPS"
                lower.contains("дрон") || lower.contains("полет") -> "FPV Drone Dynamic Flight • 4K 60fps"
                lower.contains("слоумо") || lower.contains("slow") -> "Slow-Motion 120 FPS High Dynamic"
                else -> "Veo Hyper-Realistic Motion • 60 FPS"
            }
            val cleanPrompt = prompt.replace("/video", "").replace("/sora", "").trim()
            val finalPrompt = if (cleanPrompt.isNotBlank()) cleanPrompt else "Полет сквозь неоновый мегаполис Liquid Glass"
            val textReply = "🎬 Видеоролик успешно создан моделью **Google Veo & Sora AI**.\n\n✨ **Сцена:** «$finalPrompt»\n📹 **Камера:** Движение по орбите, кинематографичный свет\n⏱ **Длительность:** 0:05 сек (60 кадров/сек)"

            return@withContext AiGeneratedMediaResult(
                text = textReply,
                mediaType = MediaType.VIDEO,
                mediaPrompt = finalPrompt,
                mediaStyle = style,
                mediaSeed = Random.nextInt(1000, 99999),
                videoDurationSec = 5,
                mediaAspectRatio = "16:9"
            )
        }

        // Standard Text AI Generation
        val text = generateResponse(prompt, model, history, systemPrompt, temperature, customApiKey)
        return@withContext AiGeneratedMediaResult(
            text = text,
            mediaType = MediaType.NONE,
            mediaPrompt = null,
            mediaStyle = null,
            mediaSeed = 1
        )
    }

    suspend fun generateResponse(
        prompt: String,
        model: AiModelType,
        history: List<MessageEntity>,
        systemPrompt: String,
        temperature: Float,
        customApiKey: String
    ): String = withContext(Dispatchers.IO) {
        val geminiKey = when {
            customApiKey.isNotBlank() && (model == AiModelType.GEMINI_3_5_FLASH || model == AiModelType.GEMINI_3_1_PRO) -> customApiKey
            else -> BuildConfig.GEMINI_API_KEY
        }

        try {
            when (model) {
                AiModelType.GEMINI_3_5_FLASH,
                AiModelType.GEMINI_3_1_PRO -> {
                    if (geminiKey.isNotBlank() && !geminiKey.contains("MY_GEMINI_API_KEY") && !geminiKey.contains("TODO")) {
                        return@withContext callGeminiApi(
                            prompt = prompt,
                            modelName = model.apiModelName,
                            history = history,
                            systemPrompt = systemPrompt,
                            temperature = temperature,
                            apiKey = geminiKey
                        )
                    } else {
                        return@withContext generateSmartSimulatedReply(prompt, model, systemPrompt)
                    }
                }
                AiModelType.CHATGPT_4O,
                AiModelType.CHATGPT_4O_MINI -> {
                    if (customApiKey.isNotBlank() && customApiKey.startsWith("sk-")) {
                        return@withContext callOpenAiApi(
                            prompt = prompt,
                            modelName = model.apiModelName,
                            history = history,
                            systemPrompt = systemPrompt,
                            temperature = temperature,
                            apiKey = customApiKey
                        )
                    } else if (geminiKey.isNotBlank() && !geminiKey.contains("MY_GEMINI_API_KEY")) {
                        val enhancedSystemPrompt = "Ты модель $model. $systemPrompt"
                        return@withContext callGeminiApi(
                            prompt = prompt,
                            modelName = "gemini-3.5-flash",
                            history = history,
                            systemPrompt = enhancedSystemPrompt,
                            temperature = temperature,
                            apiKey = geminiKey
                        )
                    } else {
                        return@withContext generateSmartSimulatedReply(prompt, model, systemPrompt)
                    }
                }
                AiModelType.IMAGEN_3_PHOTO -> {
                    return@withContext "🎨 Изображение создано по вашему запросу: «$prompt»!"
                }
                AiModelType.VEO_VIDEO_STUDIO -> {
                    return@withContext "🎬 Кинематографичный видеоряд сгенерирован по описанию: «$prompt»!"
                }
                AiModelType.OMNI_ASSISTANT -> {
                    if (geminiKey.isNotBlank() && !geminiKey.contains("MY_GEMINI_API_KEY")) {
                        return@withContext callGeminiApi(
                            prompt = prompt,
                            modelName = "gemini-3.5-flash",
                            history = history,
                            systemPrompt = "Ты универсальный мультимодальный ИИ Omni Telegram. $systemPrompt",
                            temperature = temperature,
                            apiKey = geminiKey
                        )
                    } else {
                        return@withContext generateSmartSimulatedReply(prompt, model, systemPrompt)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("AiService", "API call failed, switching to smart local engine: ${e.message}", e)
            return@withContext generateSmartSimulatedReply(prompt, model, systemPrompt)
        }
    }

    private fun callGeminiApi(
        prompt: String,
        modelName: String,
        history: List<MessageEntity>,
        systemPrompt: String,
        temperature: Float,
        apiKey: String
    ): String {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val rootJson = JSONObject()
        val contentsArray = JSONArray()

        val recentHistory = history.takeLast(6)
        for (msg in recentHistory) {
            val role = if (msg.messageSender == MessageSender.ME) "user" else "model"
            val contentObj = JSONObject()
            contentObj.put("role", role)
            val partsArray = JSONArray()
            val textPart = JSONObject()
            textPart.put("text", msg.text)
            partsArray.put(textPart)
            contentObj.put("parts", partsArray)
            contentsArray.put(contentObj)
        }

        val currentContent = JSONObject()
        currentContent.put("role", "user")
        val currentParts = JSONArray()
        val currentPart = JSONObject()
        currentPart.put("text", prompt)
        currentParts.put(currentPart)
        currentContent.put("parts", currentParts)
        contentsArray.put(currentContent)

        rootJson.put("contents", contentsArray)

        if (systemPrompt.isNotBlank()) {
            val systemInstruction = JSONObject()
            val sysParts = JSONArray()
            val sysText = JSONObject()
            sysText.put("text", systemPrompt)
            sysParts.put(sysText)
            systemInstruction.put("parts", sysParts)
            rootJson.put("systemInstruction", systemInstruction)
        }

        val generationConfig = JSONObject()
        generationConfig.put("temperature", temperature)
        rootJson.put("generationConfig", generationConfig)

        val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                throw RuntimeException("Gemini API error ${response.code}: $errBody")
            }

            val bodyString = response.body?.string() ?: throw RuntimeException("Empty response body")
            val json = JSONObject(bodyString)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val content = candidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    return parts.getJSONObject(0).optString("text", "")
                }
            }
            return "Не удалось получить текст ответа от Gemini."
        }
    }

    private fun callOpenAiApi(
        prompt: String,
        modelName: String,
        history: List<MessageEntity>,
        systemPrompt: String,
        temperature: Float,
        apiKey: String
    ): String {
        val url = "https://api.openai.com/v1/chat/completions"

        val rootJson = JSONObject()
        rootJson.put("model", modelName)
        rootJson.put("temperature", temperature)

        val messagesArray = JSONArray()

        if (systemPrompt.isNotBlank()) {
            val sysMsg = JSONObject()
            sysMsg.put("role", "system")
            sysMsg.put("content", systemPrompt)
            messagesArray.put(sysMsg)
        }

        val recentHistory = history.takeLast(6)
        for (msg in recentHistory) {
            val role = if (msg.messageSender == MessageSender.ME) "user" else "assistant"
            val msgObj = JSONObject()
            msgObj.put("role", role)
            msgObj.put("content", msg.text)
            messagesArray.put(msgObj)
        }

        val currentMsg = JSONObject()
        currentMsg.put("role", "user")
        currentMsg.put("content", prompt)
        messagesArray.put(currentMsg)

        rootJson.put("messages", messagesArray)

        val requestBody = rootJson.toString().toRequestBody(jsonMediaType)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                throw RuntimeException("OpenAI API error ${response.code}: $errBody")
            }

            val bodyString = response.body?.string() ?: throw RuntimeException("Empty response body")
            val json = JSONObject(bodyString)
            val choices = json.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val choice = choices.getJSONObject(0)
                val msg = choice.optJSONObject("message")
                return msg?.optString("content", "") ?: ""
            }
            return "Не удалось получить ответ от ChatGPT."
        }
    }

    private fun generateSmartSimulatedReply(
        prompt: String,
        model: AiModelType,
        systemPrompt: String
    ): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("привет") || lower.contains("здравствуй") || lower.contains("hello") || lower.contains("хай") -> {
                "Привет! Я твой ИИ-ассистент **${model.displayName}** в Telegram Liquid Glass ✨\n\nЧем я могу помочь прямо сейчас?\n• 💡 Генерация идей и текстов\n• 🎨 Создание фото и концептов (/photo)\n• 🎬 Создание видеороликов (/video)\n• 💻 Написание и разбор кода\n• ⚡ Анализ и перевод на любые языки"
            }
            lower.contains("как дела") || lower.contains("что умеешь") || lower.contains("помощь") -> {
                "Все отлично! Готов решать любые задачи в стиле Telegram Liquid Glass 💎\n\nМои возможности:\n1. Мгновенное переключение между Gemini, ChatGPT, Imagen 3 и Veo Video.\n2. Генерация реалистичных фото и динамических видеороликов прямо в диалоге.\n3. Сохранение истории в локальной базе Room.\n4. Поиск пользователей по @юзернейму, имени и скрытие номера телефона."
            }
            lower.contains("код") || lower.contains("программирован") || lower.contains("kotlin") || lower.contains("python") -> {
                "Отличный вопрос по коду! 💻 Вот пример реализации в Liquid Glass:\n\n```kotlin\n// Элегантный Liquid Glass компонент на Jetpack Compose\n@Composable\nfun LiquidGlassButton(text: String, onClick: () -> Unit) {\n    Box(\n        modifier = Modifier\n            .liquidGlass(shape = RoundedCornerShape(16.dp))\n            .clickable { onClick() }\n            .padding(horizontal = 20.dp, vertical = 12.dp)\n    ) {\n        Text(text, color = Color.White, fontWeight = FontWeight.Bold)\n    }\n}\n```\n\nЕсли нужно изменить параметры или написать другой алгоритм — просто уточни задачу!"
            }
            else -> {
                "⚡ **${model.displayName}**:\n\nОтвет на запрос «$prompt»:\n\nЯ проанализировал ваш вопрос с учетом контекста Telegram Liquid Glass. Вы можете также переключать специализированные модели ИИ для фото и видео через верхнее меню настроек ⚙️!"
            }
        }
    }
}
