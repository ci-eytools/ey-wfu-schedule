package com.atri.seduley.domain.result

sealed interface SystemConfResult {
    data object Success : SystemConfResult
    data object UnknownError : SystemConfResult
}