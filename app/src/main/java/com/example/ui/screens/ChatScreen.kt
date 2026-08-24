package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiModelType
import com.example.data.model.ChatType
import com.example.ui.TelegramViewModel
import com.example.ui.components.LiquidGlassMessageBubble
import com.example.ui.components.ModelSettingsSheet
import com.example.ui.components.TelegramAvatar
import com.example.ui.theme.LiquidCoral
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidPurple
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.liquidAuraBackgroundBrush
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent

@Composable
fun ChatScreen(
    viewModel: TelegramViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chat by viewModel.currentChat.collectAsStateWithLifecycle()
    val messages by viewModel.currentMessages.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGeneratingResponse.collectAsStateWithLifecycle()
    val replyingMessage by viewModel.replyingMessage.collectAsStateWithLifecycle()
    val isRecordingAudio by viewModel.isRecordingAudio.collectAsStateWithLifecycle()
    val recordingSeconds by viewModel.recordingSeconds.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val currentChatEntity = chat ?: return

    val quickPrompts = if (currentChatEntity.chatType == ChatType.AI_BOT) {
        listOf(
            "🎨 Нарисуй неоновый город в стиле киберпанк 4K",
            "🎬 Создай видео: полет сквозь северное сияние",
            "⚡ Что умеет Gemini 3.5 & ChatGPT-4o?",
            "💻 Напиши Liquid Glass анимацию на Kotlin",
            "💡 Придумай идею для мобильного стартапа"
        )
    } else {
        listOf(
            "👋 Привет!",
            "🔥 Отличный дизайн Liquid Glass!",
            "👍 Договорились",
            "🚀 Когда релиз?"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(liquidAuraBackgroundBrush())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Liquid Glass Top Navigation Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .liquidGlass(shape = RoundedCornerShape(20.dp), borderAlpha = 0.35f)
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .liquidGlass(shape = CircleShape, borderAlpha = 0.4f)
                            .clickable { onBack() }
                            .testTag("chat_back_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Title and Avatar Row
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showSettingsSheet = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TelegramAvatar(
                            emoji = currentChatEntity.avatarEmoji,
                            backgroundColor = Color(currentChatEntity.avatarColorHex),
                            sizeDp = 40,
                            isOnline = currentChatEntity.isOnline,
                            badgeText = if (currentChatEntity.chatType == ChatType.AI_BOT) "AI" else null
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentChatEntity.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                if (currentChatEntity.username.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "@${currentChatEntity.username}",
                                        color = LiquidCyan.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Text(
                                text = if (isGenerating) {
                                    if (currentChatEntity.chatType == ChatType.AI_BOT) "⚡ ИИ генерирует ответ..." else "печатает..."
                                } else {
                                    if (currentChatEntity.chatType == ChatType.AI_BOT) {
                                        "${currentChatEntity.activeModel.iconEmoji} ${currentChatEntity.activeModel.displayName}"
                                    } else {
                                        currentChatEntity.subtitle.ifBlank { "в сети" }
                                    }
                                },
                                color = if (isGenerating) LiquidCyan else Color.White.copy(alpha = 0.6f),
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Model Badge / Settings Button
                    if (currentChatEntity.chatType == ChatType.AI_BOT) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(LiquidCyan.copy(alpha = 0.2f))
                                .border(0.5.dp, LiquidCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable { showSettingsSheet = true }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = currentChatEntity.activeModel.iconEmoji,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .liquidGlass(shape = CircleShape, borderAlpha = 0.4f)
                            .clickable { showSettingsSheet = true }
                            .testTag("chat_settings_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Settings",
                            tint = LiquidCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Message Stream Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    items(
                        items = messages,
                        key = { it.id }
                    ) { message ->
                        LiquidGlassMessageBubble(
                            message = message,
                            onReactionClick = { reaction ->
                                viewModel.toggleReaction(message.id, message.reaction, reaction)
                            },
                            onReplyClick = {
                                viewModel.setReplyingMessage(message)
                            }
                        )
                    }

                    // Generating indicator pill
                    if (isGenerating) {
                        item {
                            Box(
                                modifier = Modifier
                                    .padding(start = 14.dp, top = 6.dp, bottom = 6.dp)
                                    .liquidGlass(shape = RoundedCornerShape(14.dp), borderAlpha = 0.4f)
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = LiquidCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${currentChatEntity.activeModel.displayName} генерирует ответ...",
                                        fontSize = 12.sp,
                                        color = LiquidCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Input Controls & Quick Prompts
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
            ) {
                // Quick Prompts Suggestions
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(quickPrompts) { prompt ->
                        Box(
                            modifier = Modifier
                                .liquidGlass(shape = RoundedCornerShape(14.dp), borderAlpha = 0.25f)
                                .clickable {
                                    viewModel.sendMessage(prompt)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = prompt,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Reply Preview Bar
                AnimatedVisibility(
                    visible = replyingMessage != null,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut()
                ) {
                    if (replyingMessage != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp)
                                .liquidGlass(shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(LiquidCyan)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Ответ на сообщение", color = LiquidCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(replyingMessage?.text ?: "", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { viewModel.setReplyingMessage(null) }
                                )
                            }
                        }
                    }
                }

                // Input Bar (Text / Voice / Media generation)
                if (isRecordingAudio) {
                    // Audio Recording Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .liquidGlass(shape = RoundedCornerShape(24.dp), borderAlpha = 0.5f)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val infiniteTransition = rememberInfiniteTransition(label = "rec_blink")
                                val blinkAlpha by infiniteTransition.animateFloat(
                                    initialValue = 0.2f,
                                    targetValue = 1.0f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(500),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "blink"
                                )

                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(LiquidCoral.copy(alpha = blinkAlpha))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Запись голоса: 0:${if (recordingSeconds < 10) "0$recordingSeconds" else recordingSeconds}",
                                    color = LiquidCoral,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                TextButton(onClick = { viewModel.stopAudioRecording(send = false) }) {
                                    Text("Отмена", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .liquidGlassAccent(shape = CircleShape, accentColor = LiquidCyan)
                                        .clickable { viewModel.stopAudioRecording(send = true) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Regular Text & Action Input Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // AI Model quick shortcut button
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .liquidGlass(shape = CircleShape, borderAlpha = 0.35f)
                                .clickable { showSettingsSheet = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (currentChatEntity.chatType == ChatType.AI_BOT) Icons.Default.AutoAwesome else Icons.Default.AttachFile,
                                contentDescription = "AI Options",
                                tint = LiquidCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Text input field
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .liquidGlass(shape = RoundedCornerShape(23.dp), borderAlpha = 0.35f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(modifier = Modifier.padding(horizontal = 14.dp)) {
                                if (inputText.isEmpty()) {
                                    Text(
                                        text = if (currentChatEntity.chatType == ChatType.AI_BOT) "Спросите ИИ, фото или видео..." else "Сообщение...",
                                        color = Color.White.copy(alpha = 0.45f),
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("chat_message_input")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Send / Voice Button
                        if (inputText.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .liquidGlassAccent(shape = CircleShape, accentColor = LiquidCyan)
                                    .clickable {
                                        val text = inputText
                                        inputText = ""
                                        viewModel.sendMessage(text)
                                    }
                                    .testTag("send_message_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .liquidGlass(shape = CircleShape, borderAlpha = 0.4f)
                                    .clickable { viewModel.startAudioRecording() }
                                    .testTag("record_audio_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record",
                                    tint = LiquidCyan,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSettingsSheet) {
        ModelSettingsSheet(
            chat = currentChatEntity,
            onDismiss = { showSettingsSheet = false },
            onModelSelected = { model ->
                viewModel.setModel(model)
            },
            onSaveSettings = { prompt, temp, apiKey ->
                viewModel.updateSettings(prompt, temp, apiKey)
            },
            onClearHistory = {
                viewModel.clearChatHistory()
            },
            onDeleteChat = {
                showDeleteConfirmDialog = true
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            containerColor = LiquidSapphire.copy(alpha = 0.95f),
            shape = RoundedCornerShape(20.dp),
            title = { Text("Удалить чат?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Вы уверены, что хотите безвозвратно удалить этот чат и все сообщения?", color = Color.White.copy(alpha = 0.8f)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteCurrentChat()
                        onBack()
                    }
                ) {
                    Text("Удалить", color = LiquidCoral, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Отмена", color = Color.White.copy(alpha = 0.6f))
                }
            }
        )
    }
}
