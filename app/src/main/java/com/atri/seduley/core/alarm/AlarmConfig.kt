package com.atri.seduley.core.alarm

import java.time.LocalDateTime

data class AlarmConfig(
    val requestCode: Int,
    val triggerAt: LocalDateTime,
    val windowMillis: Long? = null,
    val backend: AlarmBackend
)