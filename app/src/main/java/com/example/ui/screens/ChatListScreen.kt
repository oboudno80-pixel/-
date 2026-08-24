package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AiModelType
import com.example.data.model.ChatType
import com.example.ui.ChatCategory
import com.example.ui.TelegramViewModel
import com.example.ui.components.ApkDownloadBanner
import com.example.ui.components.LiquidGlassChatRow
import com.example.ui.components.LiquidGlassSearchBar
import com.example.ui.components.LiquidGlassTabs
import com.example.ui.components.NewChatDialog
import com.example.ui.components.ProfileSettingsSheet
import com.example.ui.theme.LiquidCoral
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidElectricBlue
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidPurple
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.liquidAuraBackgroundBrush
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent
import kotlinx.coroutines.launch

@Composable
fun ChatListScreen(
    viewModel: TelegramViewModel,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val chats by viewModel.chats.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var showNewChatDialog by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(310.dp),
                drawerContainerColor = LiquidSapphire.copy(alpha = 0.95f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(liquidAuraBackgroundBrush())
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    // Drawer Profile Glass Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .liquidGlass(
                                shape = RoundedCornerShape(20.dp),
                                backgroundColor = LiquidDeepBlue.copy(alpha = 0.5f),
                                borderAlpha = 0.4f
                            )
                            .clickable {
                                coroutineScope.launch { drawerState.close() }
                                showProfileSheet = true
                            }
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .liquidGlass(shape = CircleShape, borderAlpha = 0.6f, glowColor = LiquidCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(userProfile.avatarEmoji, fontSize = 28.sp)
                                }

                                if (userProfile.isPhoneHidden) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(LiquidCyan.copy(alpha = 0.15f))
                                            .border(0.5.dp, LiquidCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = LiquidCyan,
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "Скрыт",
                                                color = LiquidCyan,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = userProfile.fullName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = userProfile.displayUsername,
                                color = LiquidCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = userProfile.displayPhoneNumber,
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // AI Quick Studio button
                    LiquidGlassDrawerItem(
                        icon = Icons.Default.AutoAwesome,
                        title = "ИИ Помощники (Gemini & ChatGPT)",
                        badge = "Omni",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onChatClick("ai_assistant")
                        }
                    )

                    LiquidGlassDrawerItem(
                        icon = Icons.Default.Image,
                        title = "🎨 Imagen 3 • Фотостудия",
                        badge = "4K HDR",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onChatClick("ai_image_studio")
                        }
                    )

                    LiquidGlassDrawerItem(
                        icon = Icons.Default.Movie,
                        title = "🎬 Veo & Sora • Видеостудия",
                        badge = "60 FPS",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onChatClick("ai_video_studio")
                        }
                    )

                    LiquidGlassDrawerItem(
                        icon = Icons.Default.Bookmark,
                        title = "Избранное (Заметки)",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            onChatClick("saved_messages")
                        }
                    )

                    LiquidGlassDrawerItem(
                        icon = Icons.Default.Person,
                        title = "Мой профиль и приватность",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showProfileSheet = true
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // APK Download Banner inside drawer
                    ApkDownloadBanner(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    LiquidGlassDrawerItem(
                        icon = Icons.Default.Info,
                        title = "О приложении Liquid Glass",
                        onClick = {
                            coroutineScope.launch { drawerState.close() }
                            showAboutDialog = true
                        }
                    )
                }
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(liquidAuraBackgroundBrush())
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Liquid Glass Header & Search
                LiquidGlassSearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.setSearchQuery(it) },
                    onOpenProfile = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    userEmoji = userProfile.avatarEmoji
                )

                // Category Tabs in Liquid Glass
                LiquidGlassTabs(
                    categories = listOf(
                        ChatCategory.ALL,
                        ChatCategory.AI_BOTS,
                        ChatCategory.DIRECT,
                        ChatCategory.CHANNELS,
                        ChatCategory.SAVED
                    ),
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.setCategory(it) }
                )

                // Quick AI Promo Pill (if in AI or ALL tab)
                if (selectedCategory == ChatCategory.ALL || selectedCategory == ChatCategory.AI_BOTS) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                            .liquidGlassAccent(
                                shape = RoundedCornerShape(16.dp),
                                accentColor = LiquidPurple,
                                glowAlpha = 0.2f
                            )
                            .clickable { onChatClick("ai_assistant") }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(LiquidCyan.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("✨", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ИИ Студия: Текст, Фото & Видео",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "Gemini 3.5 • ChatGPT-4o • Imagen 3 • Veo",
                                        color = LiquidCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LiquidCyan)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Открыть",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Chat List or Empty State
                if (chats.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .liquidGlass(shape = CircleShape, borderAlpha = 0.5f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💬", fontSize = 32.sp)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "Ничего не найдено" else "Нет чатов в этой вкладке",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Нажмите '+' чтобы создать контакт или ИИ-бота",
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 70.dp)
                    ) {
                        item(key = "apk_download_banner_item") {
                            ApkDownloadBanner(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        items(
                            items = chats,
                            key = { it.id }
                        ) { chat ->
                            LiquidGlassChatRow(
                                chat = chat,
                                onClick = { onChatClick(chat.id) }
                            )
                        }
                    }
                }
            }

            // Liquid Glass Floating Action Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .size(56.dp)
                    .liquidGlassAccent(
                        shape = CircleShape,
                        accentColor = LiquidCyan,
                        glowAlpha = 0.4f
                    )
                    .clickable { showNewChatDialog = true }
                    .testTag("fab_new_chat"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }

    // New Chat Dialog
    if (showNewChatDialog) {
        NewChatDialog(
            onDismiss = { showNewChatDialog = false },
            onCreateChat = { title, type, emoji, username, phone, model ->
                viewModel.createChat(title, type, emoji, username, phone, model)
            }
        )
    }

    // Profile Settings Sheet
    if (showProfileSheet) {
        ProfileSettingsSheet(
            profile = userProfile,
            onDismiss = { showProfileSheet = false },
            onSaveProfile = { first, last, uname, bio ->
                viewModel.updateProfile(first, last, uname, bio)
            },
            onUpdatePrivacy = { isHidden, level ->
                viewModel.updatePhonePrivacy(isHidden, level)
            },
            onLogout = {
                viewModel.logout()
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = LiquidSapphire.copy(alpha = 0.95f),
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = LiquidCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Telegram Liquid Glass", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Мессенджер Telegram в ультрасовременном дизайне Liquid Glass:", color = Color.White.copy(alpha = 0.8f))
                    Text("• Регистрация по номеру и SMS коду с приватным скрытием номера", color = LiquidCyan)
                    Text("• Поиск по @юзернейму, телефону и названию", color = Color.White.copy(alpha = 0.8f))
                    Text("• ИИ-ассистенты: Gemini 3.5 Flash, Gemini 3.1 Pro, ChatGPT-4o Omni", color = LiquidCyan)
                    Text("• Генерация фото (Imagen 3) и видео (Veo 2.0 / Sora AI)", color = Color.White.copy(alpha = 0.8f))
                    Text("• Голосовые сообщения, реакции, системные промпты и Room база данных", color = LiquidCyan)
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .liquidGlassAccent(shape = RoundedCornerShape(12.dp), accentColor = LiquidCyan)
                        .clickable { showAboutDialog = false }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text("Отлично", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun LiquidGlassDrawerItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .liquidGlass(shape = RoundedCornerShape(14.dp), borderAlpha = 0.15f)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LiquidCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiquidViolet)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
