package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.TaskEntity
import com.atri.seduley.data.local.database.entity.TriggerMode
import com.atri.seduley.domain.model.Task

/** TaskEntity -> Task */
fun TaskEntity.toDomain(): Task {
    return Task(
        requestCode = requestCode,
        triggerAt = triggerAt,
        actualTriggerAt = actualTriggerAt,
        triggerMode = triggerMode,
        callback = callback,
        state = state,
        params = params,
        createdAt = createdAt
    )
}

/** Task -> TaskEntity */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        requestCode = requestCode,
        triggerAt = triggerAt,
        actualTriggerAt = actualTriggerAt,
        triggerMode = triggerMode ?: TriggerMode.INEXACT,
        callback = callback,
        state = state,
        params = params,
        createdAt = createdAt
    )
}