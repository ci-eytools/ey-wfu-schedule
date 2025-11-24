package com.atri.seduley.domain.result

import com.atri.seduley.domain.model.SystemConf

sealed interface SystemConfResult {
    data class Success(val value: SystemConf? = null) : SystemConfResult
    object UnknownError : SystemConfResult
}