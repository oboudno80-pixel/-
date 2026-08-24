package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Hd
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiModelType
import com.example.data.model.ChatEntity
import com.example.data.model.ChatType
import com.example.data.model.MediaType
import com.example.data.model.MessageEntity
import com.example.data.model.MessageSender
import com.example.ui.ChatCategory
import com.example.ui.theme.GlassHighlightBorder
import com.example.ui.theme.LiquidCoral
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidElectricBlue
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidPurple
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.LocalTelegramColors
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelegramAvatar(
    emoji: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    sizeDp: Int = 52,
    isOnline: Boolean = false,
    badgeText: String? = null
) {
    Box(modifier = modifier.size(sizeDp.dp)) {
        Box(
            modifier = Modifier
                .size(sizeDp.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            backgroundColor.copy(alpha = 0.9f),
                            backgroundColor.copy(alpha = 0.5f),
                            Color(0xFF0F172A)
                        )
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            backgroundColor.copy(alpha = 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = (sizeDp * 0.45).sp
            )
        }

        if (isOnline) {
            Box(
                modifier = Modifier
                    .size((sizeDp * 0.28).dp.coerceAtLeast(14.dp))
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(LiquidEmerald)
                    .border(2.dp, LiquidSapphire, CircleShape)
            )
        }

        if (badgeText != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiquidCyan)
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText,
                    color = Color.Black,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LiquidGlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onOpenProfile: () -> Unit,
    userEmoji: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Avatar Button in top bar
        Box(
            modifier = Modifier
                .size(44.dp)
                .liquidGlass(shape = CircleShape, borderAlpha = 0.5f)
                .clickable { onOpenProfile() }
                .testTag("open_profile_button"),
            contentAlignment = Alignment.Center
        ) {
            Text(userEmoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Liquid Glass Search Field
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .liquidGlass(shape = RoundedCornerShape(22.dp), borderAlpha = 0.35f),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = LiquidCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Поиск по @юзернейму, имени, тел...",
                            color = Color.White.copy(alpha = 0.45f),
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    androidx.compose.foundation.text.BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input")
                    )
                }
                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { onQueryChange("") }
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidGlassTabs(
    categories: List<ChatCategory>,
    selectedCategory: ChatCategory,
    onCategorySelected: (ChatCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    ScrollableTabRow(
        selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        containerColor = Color.Transparent,
        contentColor = LiquidCyan,
        edgePadding = 0.dp,
        divider = {}
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            val shape = RoundedCornerShape(16.dp)

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .then(
                        if (isSelected) {
                            Modifier.liquidGlassAccent(shape = shape, accentColor = LiquidCyan, glowAlpha = 0.28f)
                        } else {
                            Modifier.liquidGlass(shape = shape, borderAlpha = 0.15f)
                        }
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(category.iconEmoji, fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.title,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LiquidGlassChatRow(
    chat: ChatEntity,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tgColors = LocalTelegramColors.current
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(chat.lastMessageTime) {
        timeFormat.format(Date(chat.lastMessageTime))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .liquidGlass(
                shape = RoundedCornerShape(18.dp),
                backgroundColor = if (chat.isPinned) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.06f),
                borderAlpha = if (chat.isPinned) 0.45f else 0.2f
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            TelegramAvatar(
                emoji = chat.avatarEmoji,
                backgroundColor = Color(chat.avatarColorHex),
                sizeDp = 50,
                isOnline = chat.isOnline,
                badgeText = if (chat.chatType == ChatType.AI_BOT) "AI" else null
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = chat.title,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (chat.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = LiquidCyan,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    if (chat.username.isNotBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "@${chat.username}",
                            color = LiquidCyan.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = formattedTime,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = chat.lastMessageText,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (chat.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = LiquidCyan.copy(alpha = 0.8f),
                            modifier = Modifier
                                .size(14.dp)
                                .rotate(30f)
                        )
                    }

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(LiquidCyan)
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = chat.unreadCount.toString(),
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Message Bubble in Liquid Glass with support for:
 * 1. Rich Text
 * 2. Voice Audio
 * 3. 🎨 Generated Image Canvas (Imagen 3 / DALL-E)
 * 4. 🎬 Generated Video Player Simulation (Veo / Sora)
 */
@Composable
fun LiquidGlassMessageBubble(
    message: MessageEntity,
    onReactionClick: (String) -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isMe = message.messageSender == MessageSender.ME
    val isBot = message.messageSender == MessageSender.BOT
    val tgColors = LocalTelegramColors.current
    var showReactions by remember { mutableStateOf(false) }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) {
        timeFormat.format(Date(message.timestamp))
    }

    val bubbleShape = remember(isMe) {
        if (isMe) {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 4.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = 4.dp,
                bottomEnd = 18.dp
            )
        }
    }

    val bubbleBackground = remember(isMe, isBot, message.activeMediaType) {
        when {
            message.activeMediaType == MediaType.IMAGE -> listOf(
                Color(0x608E24AA),
                Color(0x40311B92)
            )
            message.activeMediaType == MediaType.VIDEO -> listOf(
                Color(0x6000B4D8),
                Color(0x500077B6)
            )
            isMe -> listOf(
                Color(0x750077B6),
                Color(0x850096C7)
            )
            isBot -> listOf(
                Color(0x455E35B1),
                Color(0x35311B92)
            )
            else -> listOf(
                Color(0x30FFFFFF),
                Color(0x18FFFFFF)
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 330.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = bubbleShape,
                    ambientColor = LiquidNavy,
                    spotColor = if (isMe) LiquidCyan.copy(alpha = 0.2f) else Color.Transparent
                )
                .clip(bubbleShape)
                .background(Brush.linearGradient(bubbleBackground))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = if (isMe) 0.5f else 0.3f),
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = bubbleShape
                )
                .clickable { showReactions = !showReactions }
                .padding(10.dp)
        ) {
            Column {
                // AI Model Badge
                if (message.modelBadge != null && isBot) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LiquidCyan.copy(alpha = 0.2f))
                            .border(0.5.dp, LiquidCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Model",
                            tint = LiquidCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = message.modelBadge,
                            color = LiquidCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Reply preview banner
                if (message.replyToText != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .border(
                                width = 2.dp,
                                color = LiquidCyan,
                                shape = RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = message.replyToText,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 🎨 SPECIALIZED COMPONENT: Generated Photo (Imagen 3 / DALL-E)
                if (message.activeMediaType == MediaType.IMAGE) {
                    GeneratedImageCard(
                        prompt = message.mediaPrompt ?: message.text,
                        style = message.mediaStyle ?: "Ultra 4K HDR",
                        seed = message.mediaSeed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // 🎬 SPECIALIZED COMPONENT: Generated Video Player (Veo / Sora)
                if (message.activeMediaType == MediaType.VIDEO) {
                    GeneratedVideoPlayerCard(
                        prompt = message.mediaPrompt ?: message.text,
                        style = message.mediaStyle ?: "60 FPS 4K",
                        durationSec = message.videoDurationSec,
                        seed = message.mediaSeed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Voice Waveform Message
                if (message.isVoice) {
                    VoiceMessageWaveform(durationSec = message.voiceDurationSec, isMe = isMe)
                } else if (message.text.isNotBlank()) {
                    // Regular Text Message
                    Text(
                        text = message.text,
                        color = Color.White,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time & Status Row
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedTime,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )

                    if (isMe) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = LiquidCyan,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }
            }
        }

        // Reaction badge attached to bubble
        if (message.reaction != null) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp, start = if (isMe) 0.dp else 8.dp, end = if (isMe) 8.dp else 0.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A).copy(alpha = 0.85f))
                    .border(1.dp, LiquidCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .clickable { onReactionClick(message.reaction) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(text = message.reaction, fontSize = 12.sp)
            }
        }

        // Quick Reaction Picker Bar (Animated on click)
        AnimatedVisibility(
            visible = showReactions,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .liquidGlass(shape = RoundedCornerShape(20.dp), borderAlpha = 0.5f)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("❤️", "🔥", "👍", "🎨", "🎬", "✨", "👏", "🎉").forEach { emoji ->
                    Text(
                        text = emoji,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable {
                                onReactionClick(emoji)
                                showReactions = false
                            }
                            .padding(2.dp)
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = LiquidCyan,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onReplyClick()
                            showReactions = false
                        }
                )
            }
        }
    }
}

/**
 * 🎨 Interactive Generative Photo Card (Imagen 3 / DALL-E)
 */
@Composable
fun GeneratedImageCard(
    prompt: String,
    style: String,
    seed: Int,
    modifier: Modifier = Modifier
) {
    var isSaved by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0A1128))
            .border(1.dp, LiquidCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
    ) {
        // Generative Art Canvas (Procedural futuristic landscape / neon glass render)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Deep gradient sky
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D0221),
                            Color(0xFF0F084B),
                            Color(0xFF261447),
                            Color(0xFFFF3864)
                        )
                    )
                )

                // Glowing Sun / Portal
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFE600),
                            Color(0xFFFF3864),
                            Color.Transparent
                        )
                    ),
                    radius = w * 0.28f,
                    center = Offset(w * 0.5f, h * 0.45f)
                )

                // Neon Mountains / Liquid Glass Peaks
                val path1 = Path().apply {
                    moveTo(0f, h * 0.8f)
                    lineTo(w * 0.25f, h * 0.45f)
                    lineTo(w * 0.5f, h * 0.65f)
                    lineTo(w * 0.75f, h * 0.4f)
                    lineTo(w, h * 0.75f)
                    lineTo(w, h)
                    lineTo(0f, h)
                    close()
                }
                drawPath(
                    path = path1,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xCC00F5D4), Color(0xEE0B0C10))
                    )
                )

                // Specular reflection lines (Liquid grid)
                for (i in 0..6) {
                    val lineY = h * 0.75f + (i * h * 0.04f)
                    drawLine(
                        color = Color(0xFF00E5FF).copy(alpha = 0.4f - (i * 0.05f)),
                        start = Offset(0f, lineY),
                        end = Offset(w, lineY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = LiquidCyan,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Imagen 3 • 4K HDR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("Seed #$seed", color = Color.White.copy(alpha = 0.8f), fontSize = 9.sp)
                }
            }
        }

        // Action Toolbar below photo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1B2A))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = style,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Save button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSaved) LiquidEmerald.copy(alpha = 0.2f) else LiquidCyan.copy(alpha = 0.15f))
                        .border(0.5.dp, if (isSaved) LiquidEmerald else LiquidCyan, RoundedCornerShape(8.dp))
                        .clickable { isSaved = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Download,
                            contentDescription = "Save",
                            tint = if (isSaved) LiquidEmerald else LiquidCyan,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSaved) "Сохранено" else "4K Скачать",
                            color = if (isSaved) LiquidEmerald else Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🎬 Interactive Generative Video Player Card (Veo & Sora Video AI)
 */
@Composable
fun GeneratedVideoPlayerCard(
    prompt: String,
    style: String,
    durationSec: Int = 5,
    seed: Int,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var isSaved by remember { mutableStateOf(false) }

    // Video playback loop timer
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                delay(50)
                currentProgress += 0.015f
                if (currentProgress >= 1f) {
                    currentProgress = 0f
                }
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "video_motion")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "motion"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF0A1128))
            .border(1.dp, LiquidViolet.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
    ) {
        // Video Preview Viewport
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Deep Cyberpunk Space Sky
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000814),
                            Color(0xFF001D3D),
                            Color(0xFF003566),
                            Color(0xFF7209B7)
                        )
                    )
                )

                // Dynamic Cinematic Light Beam / Motion effect
                val motionShift = if (isPlaying) (waveOffset * w) else 0f
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            LiquidCyan.copy(alpha = 0.7f),
                            LiquidViolet.copy(alpha = 0.8f),
                            Color.Transparent
                        )
                    ),
                    start = Offset((w * 0.2f + motionShift) % w, 0f),
                    end = Offset((w * 0.8f + motionShift) % w, h),
                    strokeWidth = 8.dp.toPx()
                )

                // Fluid Glass Spheres moving in space
                for (i in 1..4) {
                    val cx = (w * (i * 0.22f) + if (isPlaying) motionShift * 0.5f else 0f) % w
                    val cy = h * 0.5f + kotlin.math.sin(i + (if (isPlaying) waveOffset * 6.28f else 0f).toDouble()).toFloat() * (h * 0.15f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                LiquidCyan.copy(alpha = 0.6f),
                                LiquidViolet.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        radius = (20 + i * 8).dp.toPx(),
                        center = Offset(cx, cy)
                    )
                }
            }

            // Header info tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .border(0.5.dp, LiquidViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = LiquidViolet,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Veo 2.0 • 60 FPS • 4K", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("0:0${(currentProgress * durationSec).toInt()} / 0:0$durationSec", color = Color.White, fontSize = 9.sp)
                }
            }

            // Center Play / Pause Floating Glass Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.Center)
                    .liquidGlass(shape = CircleShape, borderAlpha = 0.6f)
                    .clickable { isPlaying = !isPlaying },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = LiquidCyan,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Bottom Scrubbing Timeline Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(currentProgress)
                        .height(3.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(LiquidCyan, LiquidViolet)
                            )
                        )
                )
            }
        }

        // Bottom Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D1B2A))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = style,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp
            )

            // Save MP4 Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSaved) LiquidEmerald.copy(alpha = 0.2f) else LiquidViolet.copy(alpha = 0.2f))
                    .border(0.5.dp, if (isSaved) LiquidEmerald else LiquidViolet, RoundedCornerShape(8.dp))
                    .clickable { isSaved = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Check else Icons.Default.Download,
                        contentDescription = "Download MP4",
                        tint = if (isSaved) LiquidEmerald else LiquidViolet,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isSaved) "MP4 Сохранен" else "Скачать MP4",
                        color = if (isSaved) LiquidEmerald else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Animated Voice Message Bubble
 */
@Composable
fun VoiceMessageWaveform(
    durationSec: Int,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isMe) LiquidCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.2f))
                .clickable { isPlaying = !isPlaying },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play voice",
                tint = if (isMe) LiquidCyan else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Animated wave bars
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(2.5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = remember { listOf(8, 14, 22, 10, 18, 26, 12, 16, 24, 18, 10, 14, 20, 8) }
            heights.forEachIndexed { index, baseHeight ->
                val barHeight = if (isPlaying && index % 2 == 0) (baseHeight * 1.3f).dp else baseHeight.dp
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isMe) LiquidCyan.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.7f)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "0:${if (durationSec < 10) "0$durationSec" else durationSec}",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
