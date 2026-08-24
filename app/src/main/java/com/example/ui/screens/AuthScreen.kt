package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PhonePrivacyLevel
import com.example.ui.TelegramViewModel
import com.example.ui.components.ApkDownloadBanner
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
import kotlinx.coroutines.delay

data class CountryCode(val name: String, val code: String, val flag: String)

val countryCodes = listOf(
    CountryCode("Россия / Казахстан", "+7", "🇷🇺"),
    CountryCode("Беларусь", "+375", "🇧🇾"),
    CountryCode("Узбекистан", "+998", "🇺🇿"),
    CountryCode("Украина", "+380", "🇺🇦"),
    CountryCode("США / Канада", "+1", "🇺🇸"),
    CountryCode("Великобритания", "+44", "🇬🇧"),
    CountryCode("Германия", "+49", "🇩🇪"),
    CountryCode("ОАЭ", "+971", "🇦🇪")
)

enum class AuthStep {
    PHONE_INPUT,
    CODE_VERIFICATION,
    PROFILE_SETUP
}

@Composable
fun AuthScreen(
    viewModel: TelegramViewModel,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(AuthStep.PHONE_INPUT) }

    // Registration states
    var selectedCountry by remember { mutableStateOf(countryCodes[0]) }
    var phoneNumberInput by remember { mutableStateOf("999 123-45-67") }
    var isPhoneHidden by remember { mutableStateOf(true) }
    var phonePrivacyLevel by remember { mutableStateOf(PhonePrivacyLevel.NOBODY) }

    // SMS code states
    var smsCode by remember { mutableStateOf("") }
    var timerSeconds by remember { mutableIntStateOf(45) }

    // Profile states
    var firstName by remember { mutableStateOf("Павел") }
    var lastName by remember { mutableStateOf("Дуров") }
    var username by remember { mutableStateOf("pavel_tg") }
    var bio by remember { mutableStateOf("Telegram Liquid Glass • ИИ Фото & Видео ✨") }
    var avatarEmoji by remember { mutableStateOf("⚡") }
    var avatarColor by remember { mutableStateOf(0xFF00B4D8) }

    // Timer countdown
    LaunchedEffect(currentStep) {
        if (currentStep == AuthStep.CODE_VERIFICATION) {
            timerSeconds = 45
            while (timerSeconds > 0) {
                delay(1000)
                timerSeconds -= 1
            }
        }
    }

    val fullPhoneNumber = "${selectedCountry.code} $phoneNumberInput"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(liquidAuraBackgroundBrush())
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Step Container with smooth transitions
        AnimatedVisibility(
            visible = currentStep == AuthStep.PHONE_INPUT,
            enter = fadeIn() + slideInHorizontally { -it },
            exit = fadeOut() + slideOutHorizontally { -it }
        ) {
            PhoneInputStep(
                selectedCountry = selectedCountry,
                onCountryChange = { selectedCountry = it },
                phoneNumber = phoneNumberInput,
                onPhoneChange = { phoneNumberInput = it },
                isPhoneHidden = isPhoneHidden,
                onHiddenToggle = { isPhoneHidden = it },
                onContinue = {
                    if (phoneNumberInput.isNotBlank()) {
                        currentStep = AuthStep.CODE_VERIFICATION
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = currentStep == AuthStep.CODE_VERIFICATION,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { -it }
        ) {
            CodeVerificationStep(
                phoneNumber = fullPhoneNumber,
                code = smsCode,
                onCodeChange = { smsCode = it },
                timerSeconds = timerSeconds,
                onBack = { currentStep = AuthStep.PHONE_INPUT },
                onVerifySuccess = {
                    currentStep = AuthStep.PROFILE_SETUP
                }
            )
        }

        AnimatedVisibility(
            visible = currentStep == AuthStep.PROFILE_SETUP,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it }
        ) {
            ProfileSetupStep(
                phoneNumber = fullPhoneNumber,
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                username = username,
                onUsernameChange = { username = it },
                bio = bio,
                onBioChange = { bio = it },
                avatarEmoji = avatarEmoji,
                onAvatarEmojiChange = { avatarEmoji = it },
                isPhoneHidden = isPhoneHidden,
                onHiddenToggle = { isPhoneHidden = it },
                privacyLevel = phonePrivacyLevel,
                onPrivacyLevelChange = { phonePrivacyLevel = it },
                onComplete = {
                    viewModel.completeRegistration(
                        phone = fullPhoneNumber,
                        isPhoneHidden = isPhoneHidden,
                        privacyLevel = phonePrivacyLevel,
                        firstName = firstName,
                        lastName = lastName,
                        username = username,
                        bio = bio,
                        avatarEmoji = avatarEmoji,
                        avatarColor = avatarColor
                    )
                }
            )
        }
    }
}

/**
 * STEP 1: Phone Number Input
 */
@Composable
fun PhoneInputStep(
    selectedCountry: CountryCode,
    onCountryChange: (CountryCode) -> Unit,
    phoneNumber: String,
    onPhoneChange: (String) -> Unit,
    isPhoneHidden: Boolean,
    onHiddenToggle: (Boolean) -> Unit,
    onContinue: () -> Unit
) {
    var showCountryPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prominent APK & Web Portal Link Banner
        ApkDownloadBanner(modifier = Modifier.padding(bottom = 16.dp))

        Spacer(modifier = Modifier.height(10.dp))

        // Glowing Telegram Liquid Glass Logo
        Box(
            modifier = Modifier
                .size(80.dp)
                .liquidGlass(shape = CircleShape, borderAlpha = 0.6f, glowColor = LiquidCyan),
            contentAlignment = Alignment.Center
        ) {
            Text("✈️", fontSize = 38.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Ваш телефон",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Подтвердите код страны и введите свой номер телефона для входа в Telegram Liquid Glass.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Country Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(18.dp))
                .clickable { showCountryPicker = !showCountryPicker }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedCountry.flag, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedCountry.name,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = selectedCountry.code,
                    color = LiquidCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Country Dropdown list
        AnimatedVisibility(visible = showCountryPicker) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .liquidGlass(shape = RoundedCornerShape(16.dp))
                    .padding(8.dp)
            ) {
                countryCodes.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCountryChange(country)
                                showCountryPicker = false
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(country.flag, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(country.name, color = Color.White, fontSize = 14.sp)
                        }
                        Text(country.code, color = LiquidCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phone Input Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(18.dp), borderAlpha = 0.4f)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedCountry.code,
                    color = LiquidCyan,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(10.dp))
                BasicTextField(
                    value = phoneNumber,
                    onValueChange = onPhoneChange,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("phone_number_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔒 "Скрыть номер чтобы никто не видел" Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = LiquidDeepBlue.copy(alpha = 0.4f),
                    borderAlpha = 0.35f
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LiquidCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Hide phone",
                            tint = LiquidCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Скрыть номер телефона",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Никто в Telegram не увидит ваш номер",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }

                Switch(
                    checked = isPhoneHidden,
                    onCheckedChange = onHiddenToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LiquidCyan,
                        checkedTrackColor = LiquidNavy,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.testTag("hide_phone_toggle")
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Continue Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .liquidGlassAccent(shape = RoundedCornerShape(26.dp), accentColor = LiquidCyan)
                .clickable { onContinue() }
                .testTag("continue_phone_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Продолжить",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * STEP 2: SMS Verification Code
 */
@Composable
fun CodeVerificationStep(
    phoneNumber: String,
    code: String,
    onCodeChange: (String) -> Unit,
    timerSeconds: Int,
    onBack: () -> Unit,
    onVerifySuccess: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .liquidGlass(shape = CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Security Lock Visual
        Box(
            modifier = Modifier
                .size(80.dp)
                .liquidGlass(shape = CircleShape, borderAlpha = 0.6f, glowColor = LiquidViolet),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Security",
                tint = LiquidCyan,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Код подтверждения",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Мы отправили SMS с 5-значным кодом на номер\n$phoneNumber",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 5-digit PIN cells
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val targetLength = 5
            for (i in 0 until targetLength) {
                val digit = if (i < code.length) code[i].toString() else ""
                val isCurrent = i == code.length

                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .size(50.dp)
                        .liquidGlass(
                            shape = RoundedCornerShape(14.dp),
                            backgroundColor = if (isCurrent) LiquidCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                            borderAlpha = if (isCurrent) 0.8f else 0.3f
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = digit,
                        color = if (digit.isNotEmpty()) LiquidCyan else Color.White.copy(alpha = 0.4f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Hidden actual text input for keyboard entry
        BasicTextField(
            value = code,
            onValueChange = {
                if (it.length <= 5 && it.all { ch -> ch.isDigit() }) {
                    onCodeChange(it)
                    if (it.length == 5) {
                        onVerifySuccess()
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("sms_code_input")
        )

        // Quick autofill chip (Convenient simulation)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(LiquidCyan.copy(alpha = 0.15f))
                .border(1.dp, LiquidCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable {
                    onCodeChange("77492")
                    onVerifySuccess()
                }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "⚡ Вставить код из SMS: 77492",
                color = LiquidCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (timerSeconds > 0) {
            Text(
                text = "Отправить код повторно через 0:${if (timerSeconds < 10) "0$timerSeconds" else timerSeconds}",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp
            )
        } else {
            Text(
                text = "Отправить код повторно",
                color = LiquidCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onCodeChange("") }
            )
        }
    }
}

/**
 * STEP 3: Profile Setup (Имя, Ник, @Юзернейм, Скрытие номера)
 */
@Composable
fun ProfileSetupStep(
    phoneNumber: String,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    avatarEmoji: String,
    onAvatarEmojiChange: (String) -> Unit,
    isPhoneHidden: Boolean,
    onHiddenToggle: (Boolean) -> Unit,
    privacyLevel: PhonePrivacyLevel,
    onPrivacyLevelChange: (PhonePrivacyLevel) -> Unit,
    onComplete: () -> Unit
) {
    val availableEmojis = listOf("⚡", "👑", "💎", "🤖", "🚀", "🎨", "🎬", "✨", "🔥")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Информация о себе",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Введите ваше имя, выберите юзернейм @username и аватар для общения.",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar selector in Liquid Glass
        Box(
            modifier = Modifier
                .size(84.dp)
                .liquidGlass(shape = CircleShape, borderAlpha = 0.6f, glowColor = LiquidCyan),
            contentAlignment = Alignment.Center
        ) {
            Text(avatarEmoji, fontSize = 40.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Emoji Quick Picker Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            availableEmojis.forEach { emoji ->
                val isSel = emoji == avatarEmoji
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isSel) LiquidCyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.08f))
                        .border(if (isSel) 1.dp else 0.dp, LiquidCyan, CircleShape)
                        .clickable { onAvatarEmojiChange(emoji) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(emoji, fontSize = 16.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Name Fields
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text("Имя", color = LiquidCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                BasicTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("first_name_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text("Фамилия (необязательно)", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                BasicTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 15.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Username Field with @ prefix & availability
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(16.dp), borderAlpha = 0.45f)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Юзернейм (по нему вас найдут в поиске)", color = LiquidCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text("✓ Доступен", color = LiquidEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("@", color = LiquidCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    BasicTextField(
                        value = username,
                        onValueChange = onUsernameChange,
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bio Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(shape = RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text("О себе", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                BasicTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Privacy Section: "Кто видит номер телефона"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .liquidGlass(
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = LiquidDeepBlue.copy(alpha = 0.4f),
                    borderAlpha = 0.35f
                )
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Privacy",
                        tint = LiquidCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Приватность: Кто видит мой номер?",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                PhonePrivacyLevel.entries.forEach { level ->
                    val isSelected = level == privacyLevel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onPrivacyLevelChange(level)
                                onHiddenToggle(level == PhonePrivacyLevel.NOBODY)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                onPrivacyLevelChange(level)
                                onHiddenToggle(level == PhonePrivacyLevel.NOBODY)
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = LiquidCyan,
                                unselectedColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = level.label,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                            Text(
                                text = level.description,
                                color = Color.White.copy(alpha = 0.45f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Complete Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .liquidGlassAccent(shape = RoundedCornerShape(26.dp), accentColor = LiquidCyan)
                .clickable { onComplete() }
                .testTag("complete_registration_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Завершить регистрацию",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
