package com.atri.seduley.domain.result

import com.atri.seduley.domain.model.SystemConf
import kotlinx.coroutines.flow.Flow

sealed interface SystemConfResult {
    data class Success(val value: Flow<SystemConf>? = null) : SystemConfResult
    object UnknownError : SystemConfResult
}