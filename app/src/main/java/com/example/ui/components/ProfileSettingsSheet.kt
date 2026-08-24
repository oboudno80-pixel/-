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
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.PhonePrivacyLevel
import com.example.data.model.UserProfile
import com.example.ui.theme.LiquidCoral
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDeepBlue
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidNavy
import com.example.ui.theme.LiquidSapphire
import com.example.ui.theme.LiquidViolet
import com.example.ui.theme.liquidGlass
import com.example.ui.theme.liquidGlassAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsSheet(
    profile: UserProfile,
    onDismiss: () -> Unit,
    onSaveProfile: (firstName: String, lastName: String, username: String, bio: String) -> Unit,
    onUpdatePrivacy: (isPhoneHidden: Boolean, level: PhonePrivacyLevel) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isEditing by remember { mutableStateOf(false) }
    var editFirstName by remember { mutableStateOf(profile.firstName) }
    var editLastName by remember { mutableStateOf(profile.lastName) }
    var editUsername by remember { mutableStateOf(profile.username) }
    var editBio by remember { mutableStateOf(profile.bio) }

    var isHidden by remember { mutableStateOf(profile.isPhoneHidden) }
    var privacyLevel by remember { mutableStateOf(profile.phonePrivacyLevel) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = LiquidSapphire.copy(alpha = 0.95f),
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Indicator & Header
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Мой профиль",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Avatar Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .liquidGlass(shape = CircleShape, borderAlpha = 0.6f, glowColor = LiquidCyan),
                contentAlignment = Alignment.Center
            ) {
                Text(profile.avatarEmoji, fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.fullName,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = profile.displayUsername,
                color = LiquidCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Phone & Privacy Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(18.dp),
                        backgroundColor = LiquidDeepBlue.copy(alpha = 0.4f),
                        borderAlpha = 0.35f
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = LiquidCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Номер телефона",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 13.sp
                            )
                        }

                        if (profile.isPhoneHidden || profile.phonePrivacyLevel == PhonePrivacyLevel.NOBODY) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LiquidCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("🔒 Скрыт", color = LiquidCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = profile.displayPhoneNumber,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Privacy selection inside profile
                    Text(
                        text = "Кто видит номер телефона:",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    PhonePrivacyLevel.entries.forEach { level ->
                        val isSelected = level == privacyLevel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    privacyLevel = level
                                    isHidden = level == PhonePrivacyLevel.NOBODY
                                    onUpdatePrivacy(isHidden, level)
                                }
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    privacyLevel = level
                                    isHidden = level == PhonePrivacyLevel.NOBODY
                                    onUpdatePrivacy(isHidden, level)
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = LiquidCyan)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = level.label,
                                color = if (isSelected) LiquidCyan else Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Info & Bio Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(shape = RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Личные данные",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (isEditing) "Сохранить" else "Изменить",
                            color = LiquidCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                if (isEditing) {
                                    onSaveProfile(editFirstName, editLastName, editUsername, editBio)
                                    isEditing = false
                                } else {
                                    isEditing = true
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!isEditing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = LiquidCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(profile.displayUsername, color = Color.White, fontSize = 14.sp)
                        }

                        if (profile.bio.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(profile.bio, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                            }
                        }
                    } else {
                        // Editable fields
                        Text("Имя", color = LiquidCyan, fontSize = 11.sp)
                        BasicTextField(
                            value = editFirstName,
                            onValueChange = { editFirstName = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Фамилия", color = LiquidCyan, fontSize = 11.sp)
                        BasicTextField(
                            value = editLastName,
                            onValueChange = { editLastName = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Юзернейм (@username)", color = LiquidCyan, fontSize = 11.sp)
                        BasicTextField(
                            value = editUsername,
                            onValueChange = { editUsername = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("О себе", color = LiquidCyan, fontSize = 11.sp)
                        BasicTextField(
                            value = editBio,
                            onValueChange = { editBio = it },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Log out Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .liquidGlass(
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = LiquidCoral.copy(alpha = 0.15f),
                        borderAlpha = 0.3f
                    )
                    .clickable {
                        onDismiss()
                        onLogout()
                    }
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = LiquidCoral,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Выйти из аккаунта",
                        color = LiquidCoral,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
