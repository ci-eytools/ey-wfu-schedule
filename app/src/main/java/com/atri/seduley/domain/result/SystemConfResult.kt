package com.atri.seduley.domain.result

sealed interface SystemConfResult {
    data class Success<out T>(val value: T? = null) : SystemConfResult
    object UnknownError : SystemConfResult
}