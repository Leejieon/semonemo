package com.semonemo.presentation.util

object Validator {
    private val nickNameRegex = "^[가-힣a-zA-Z0-9_]{1,15}$".toRegex()
    private val passwordRegex = "[a-zA-z0-9]{1,12}$".toRegex()

    fun validationNickname(nickname: String): String =
        when {
            nickname.isEmpty() -> ""
            nickname.isBlank() -> ErrorMessage.EMPTY_MESSAGE
            nickname.length < 2 -> ErrorMessage.TOO_SHORT
            nickname.length > 10 -> ErrorMessage.NICK_TOO_LONG
            !nickNameRegex.matches(nickname) -> ErrorMessage.NICK_NOT_MATCH
            else -> ""
        }

    fun validationPassword(password: String): String =
        when {
            password.isEmpty() -> ""
            password.isBlank() -> ErrorMessage.EMPTY_MESSAGE
            password.length < 2 -> ErrorMessage.TOO_SHORT
            password.length > 15 -> ErrorMessage.PW_TOO_LONG
            !passwordRegex.matches(password) -> ErrorMessage.PW_NOT_MATCH
            else -> ""
        }
}
