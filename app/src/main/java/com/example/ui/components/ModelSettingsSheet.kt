package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModelType
import com.example.data.model.ChatEntity
import com.example.ui.theme.LiquidCoral
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidElectricBlue
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidPurple
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSettingsSheet(
    chat: ChatEntity,
    onDismiss: () -> Unit,
    onModelSelected: (AiModelType) -> Unit,
    onSaveSettings: (systemPrompt: String, temperature: Float, customApiKey: String) -> Unit,
    onClearHistory: () -> Unit,
    onDeleteChat: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedModel by remember { mutableStateOf(chat.activeModel) }
    var systemPrompt by remember { mutableStateOf(chat.systemPrompt) }
    var temperature by remember { mutableFloatStateOf(chat.temperature) }
    var customApiKey by remember { mutableStateOf(chat.customApiKey) }

    val personaPresets = listOf(
        "🤖 Универсальный" to "Ты умный, полезный и дружелюбный ИИ-помощник в Telegram Liquid Glass.",
        "🎨 Фото-генератор" to "Ты специалист по генерации фото Imagen 3 / DALL-E. Создавай детализированные 4K арты.",
        "🎬 Видео-студия" to "Ты режиссер и оператор Veo & Sora AI. Создавай кинематографичные видеосцены 60fps.",
        "💻 Программист" to "Ты Senior Software Engineer. Отвечай чистым Kotlin / Python кодом с пояснениями.",
        "⚡ Лаконичный" to "Отвечай максимально кратко, емко, без лишних вступлений."
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LiquidSapphire.copy(alpha = 0.95f),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = Modifier.testTag("model_settings_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp, top = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Indicator
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = LiquidCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Настройки ИИ & Студии",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.7f))
                }
            }

            Text(
                text = "Переключение моделей ИИ: текст, фотостудия (Imagen 3), видеостудия (Veo & Sora) и системные инструкции.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Model Selection Section
            Text(
                text = "ВЫБОР ИИ МОДЕЛИ",
                color = LiquidCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            AiModelType.entries.forEach { model ->
                val isSelected = model == selectedModel

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .then(
                            if (isSelected) {
                                Modifier.liquidGlassAccent(
                                    shape = RoundedCornerShape(16.dp),
                                    accentColor = if (model.id.contains("photo")) LiquidPurple else if (model.id.contains("video")) LiquidCyan else LiquidElectricBlue,
                                    glowAlpha = 0.3f
                                )
                            } else {
                                Modifier.liquidGlass(shape = RoundedCornerShape(16.dp), borderAlpha = 0.2f)
                            }
                        )
                        .clickable {
                            selectedModel = model
                            onModelSelected(model)
                        }
                        .padding(12.dp)
                        .testTag("model_card_${model.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = model.iconEmoji,
                            fontSize = 26.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = model.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LiquidCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = model.provider,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LiquidCyan
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = model.description,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                lineHeight = 16.sp
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = LiquidCyan,
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // System Prompt & Persona
            Text(
                text = "СИСТЕМНЫЙ ПРОМПТ / ПЕРСОНА",
                color = LiquidCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Presets row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                personaPresets.take(3).forEach { (name, promptText) ->
                    Box(
                        modifier = Modifier
                            .liquidGlass(shape = RoundedCornerShape(12.dp), borderAlpha = 0.25f)
                            .clickable { systemPrompt = promptText }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text("Инструкция для ИИ (System Prompt)", color = LiquidCyan, fontSize = 11.sp)
                    BasicTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(top = 4.dp)
                            .testTag("system_prompt_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Temperature / Creativity Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "КРЕАТИВНОСТЬ (TEMPERATURE)",
                    color = LiquidCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = String.format("%.1f", temperature),
                    fontWeight = FontWeight.Bold,
                    color = LiquidCyan,
                    fontSize = 13.sp
                )
            }

            Slider(
                value = temperature,
                onValueChange = { temperature = it },
                valueRange = 0.0f..1.5f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = LiquidCyan,
                    activeTrackColor = LiquidCyan,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.testTag("temperature_slider")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Custom API Key input (Optional)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text("Свой API ключ (OpenAI sk-... или Gemini)", color = LiquidCyan, fontSize = 11.sp)
                    BasicTextField(
                        value = customApiKey,
                        onValueChange = { customApiKey = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .testTag("custom_api_key_input")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .liquidGlassAccent(shape = RoundedCornerShape(25.dp), accentColor = LiquidCyan)
                .clickable {
                    onSaveSettings(systemPrompt, temperature, customApiKey)
                    onDismiss()
                }
                .testTag("save_settings_button"),
                contentAlignment = Alignment.Center
            ) {
                Text("Сохранить параметры", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Danger actions: Clear history & Delete chat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlass(shape = RoundedCornerShape(14.dp), borderAlpha = 0.25f)
                        .clickable {
                            onClearHistory()
                            onDismiss()
                        }
                        .padding(12.dp)
                        .testTag("clear_history_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Очистить историю", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .liquidGlass(shape = RoundedCornerShape(14.dp), backgroundColor = LiquidCoral.copy(alpha = 0.2f), borderAlpha = 0.4f)
                        .clickable {
                            onDeleteChat()
                            onDismiss()
                        }
                        .padding(12.dp)
                        .testTag("delete_chat_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = LiquidCoral, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Удалить чат", fontSize = 12.sp, color = LiquidCoral, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
