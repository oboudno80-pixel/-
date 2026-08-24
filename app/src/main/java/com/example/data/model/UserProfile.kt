package com.example.data.model

data class UserProfile(
    val phoneNumber: String = "+7 999 123-45-67",
    val isPhoneHidden: Boolean = true,
    val phonePrivacyLevel: PhonePrivacyLevel = PhonePrivacyLevel.NOBODY,
    val firstName: String = "Павел",
    val lastName: String = "Дуров",
    val username: String = "pavel_tg",
    val bio: String = "Использую Telegram Liquid Glass с поддержкой ИИ Фото & Видео ✨",
    val avatarEmoji: String = "⚡",
    val avatarColorHex: Long = 0xFF24A1DE,
    val isRegistered: Boolean = false
) {
    val isLoggedIn: Boolean
        get() = isRegistered

    val fullName: String
        get() = if (lastName.isBlank()) firstName else "$firstName $lastName"

    val displayPhoneNumber: String
        get() = if (isPhoneHidden || phonePrivacyLevel == PhonePrivacyLevel.NOBODY) {
            maskPhoneNumber(phoneNumber)
        } else {
            phoneNumber
        }

    val displayUsername: String
        get() = if (username.startsWith("@")) username else "@$username"

    companion object {
        fun maskPhoneNumber(phone: String): String {
            if (phone.length < 6) return "••••••"
            val digits = phone.filter { it.isDigit() || it == '+' }
            return if (digits.length >= 10) {
                val prefix = digits.take(2)
                val suffix = digits.takeLast(2)
                "$prefix ••• ••• $suffix (Скрыт)"
            } else {
                "••• ••• •• (Скрыт)"
            }
        }
    }
}

enum class PhonePrivacyLevel(val label: String, val description: String) {
    NOBODY("Никто", "Ваш номер полностью скрыт от всех пользователей"),
    CONTACTS("Мои контакты", "Номер видят только добавленные контакты"),
    EVERYONE("Все", "Любой пользователь может видеть ваш номер")
}
