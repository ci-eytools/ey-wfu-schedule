package com.atri.seduley.domain.model

import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.data.local.database.entity.TriggerMode
import java.time.LocalDateTime

data class Task(
    val requestCode: Int,
    val triggerAt: LocalDateTime,
    val triggerMode: TriggerMode,
    val callback: Callback,
    val state: TaskState,
    val params: Map<String, String>,
    val updatedAt: LocalDateTime
)