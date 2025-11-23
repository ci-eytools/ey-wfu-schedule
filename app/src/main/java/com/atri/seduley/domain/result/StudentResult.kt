package com.atri.seduley.domain.result

import com.atri.seduley.domain.model.Student

sealed interface StudentResult {
    data class Success(val student: Student) : StudentResult
    data class AuthError(val msg: String) : StudentResult
    data object UnknownError : StudentResult
}