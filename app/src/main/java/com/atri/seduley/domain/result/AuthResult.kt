package com.atri.seduley.domain.result

sealed interface AuthResult {
    data object Success : AuthResult
    data class InvalidCredential(val msg: String): AuthResult
    data object NetworkError: AuthResult
    data object UnknownError: AuthResult
}