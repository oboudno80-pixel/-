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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModelType
import com.example.data.model.ChatType
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidPurple
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent

@Composable
fun NewChatDialog(
    onDismiss: () -> Unit,
    onCreateChat: (title: String, type: ChatType, avatarEmoji: String, username: String, phone: String, model: AiModelType) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ChatType.AI_BOT) }
    var selectedEmoji by remember { mutableStateOf("🤖") }
    var selectedModel by remember { mutableStateOf(AiModelType.GEMINI_3_5_FLASH) }

    val emojis = listOf("🤖", "🎨", "🎬", "⚡", "👑", "💬", "🚀", "💎", "💡", "🎯")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LiquidSapphire.copy(alpha = 0.95f),
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = LiquidCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Создать новый чат",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Type selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        ChatType.AI_BOT to "🤖 ИИ Бот",
                        ChatType.DIRECT to "👤 Контакт",
                        ChatType.CHANNEL to "📢 Канал"
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (isSelected) Modifier.liquidGlassAccent(shape = RoundedCornerShape(12.dp), accentColor = LiquidCyan)
                                    else Modifier.liquidGlass(shape = RoundedCornerShape(12.dp), borderAlpha = 0.2f)
                                )
                                .clickable {
                                    selectedType = type
                                    if (type == ChatType.AI_BOT) selectedEmoji = "🤖"
                                    if (type == ChatType.DIRECT) selectedEmoji = "💬"
                                    if (type == ChatType.CHANNEL) selectedEmoji = "📢"
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Chat title input
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .liquidGlass(shape = RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = when (selectedType) {
                                ChatType.AI_BOT -> "Имя ИИ-ассистента"
                                ChatType.DIRECT -> "Имя контакта"
                                ChatType.CHANNEL -> "Название канала"
                                ChatType.GROUP -> "Название группы"
                            },
                            color = LiquidCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        BasicTextField(
                            value = title,
                            onValueChange = { title = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                                .testTag("new_chat_title_input")
                        )
                    }
                }

                // Username / Phone for direct contacts
                if (selectedType == ChatType.DIRECT) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(shape = RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Юзернейм (@username)", color = LiquidCyan, fontSize = 10.sp)
                            BasicTextField(
                                value = username,
                                onValueChange = { username = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(shape = RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("Номер телефона (+7 ...)", color = LiquidCyan, fontSize = 10.sp)
                            BasicTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Emoji picker
                Text(
                    text = "Выберите аватар:",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    emojis.take(5).forEach { emoji ->
                        val isSel = selectedEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSel) LiquidCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                                .border(if (isSel) 1.dp else 0.dp, LiquidCyan, CircleShape)
                                .clickable { selectedEmoji = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 16.sp)
                        }
                    }
                }

                // If AI Bot, model selector
                if (selectedType == ChatType.AI_BOT) {
                    Text(
                        text = "Специализация ИИ:",
                        color = LiquidCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            AiModelType.GEMINI_3_5_FLASH to "⚡ Gemini",
                            AiModelType.IMAGEN_3_PHOTO to "🎨 Фото",
                            AiModelType.VEO_VIDEO_STUDIO to "🎬 Видео"
                        ).forEach { (m, lbl) ->
                            val isSel = selectedModel == m
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .then(
                                        if (isSel) Modifier.liquidGlassAccent(shape = RoundedCornerShape(10.dp), accentColor = LiquidCyan)
                                        else Modifier.liquidGlass(shape = RoundedCornerShape(10.dp), borderAlpha = 0.15f)
                                    )
                                    .clickable { selectedModel = m }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lbl,
                                    color = if (isSel) Color.White else Color.White.copy(alpha = 0.6f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier
                    .liquidGlassAccent(shape = RoundedCornerShape(14.dp), accentColor = LiquidCyan)
                    .clickable(enabled = title.isNotBlank()) {
                        onCreateChat(
                            title.trim(),
                            selectedType,
                            selectedEmoji,
                            username.trim().removePrefix("@"),
                            phoneNumber.trim(),
                            selectedModel
                        )
                        onDismiss()
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("confirm_create_chat_button"),
                contentAlignment = Alignment.Center
            ) {
                Text("Создать", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
