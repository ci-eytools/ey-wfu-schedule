package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.TaskEntity
import com.atri.seduley.domain.model.Task

/** TaskEntity -> Task */
fun TaskEntity.toDomain(): Task {
    return Task(
        requestCode = requestCode,
        triggerAt = triggerAt,
        triggerMode = triggerMode,
        callback = callback,
        state = state,
        params = params,
        updatedAt = updatedAt
    )
}

/** Task -> TaskEntity */
fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        requestCode = requestCode,
        triggerAt = triggerAt,
        triggerMode = triggerMode,
        callback = callback,
        state = state,
        params = params,
        updatedAt = updatedAt
    )
}