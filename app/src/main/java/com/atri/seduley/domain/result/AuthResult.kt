package com.atri.seduley.domain.result

import com.atri.seduley.domain.model.Student

sealed interface AuthResult {
    data class Success(val value: Student? = null) : AuthResult
    data class InvalidCredential(val msg: String) : AuthResult
    data object NetworkError : AuthResult
    data class UnknownError(val msg: String? = null) : AuthResult
}