package com.atri.seduley.domain.model

import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.data.local.database.entity.TriggerMode
import java.time.LocalDateTime

data class Task(
    val requestCode: Int,
    val triggerAt: LocalDateTime,
    val actualTriggerAt: LocalDateTime? = null,
    val triggerMode: TriggerMode?,
    val callback: Callback,
    val state: TaskState,
    val params: Map<String, String>,
    val createdAt: LocalDateTime
)

fun Task.copyWithNewParams(
    requestCode: Int = this.requestCode,
    triggerAt: LocalDateTime = this.triggerAt,
    actualTriggerAt: LocalDateTime? = this.actualTriggerAt,
    triggerMode: TriggerMode? = this.triggerMode,
    callback: Callback = this.callback,
    state: TaskState = this.state,
    newParams: Map<String, String>, // 专门用于接收新的参数
    createdAt: LocalDateTime = this.createdAt
) = copy(
    requestCode = requestCode,
    triggerAt = triggerAt,
    actualTriggerAt = actualTriggerAt,
    triggerMode = triggerMode,
    callback = callback,
    state = state,
    // 合并旧的params和新的newParams
    params = this.params + newParams,
    createdAt = createdAt
)

fun Task.toDone() = copy(state = TaskState.DONE)

/** 自动更新目标触发时间与实际触发时间 */
fun Task.nextDay() = copy(
    triggerAt = triggerAt.plusDays(1),
    actualTriggerAt = LocalDateTime.now(),
    state = TaskState.AWAIT
)

fun Task.toTimeOut() = copy(state = TaskState.TIME_OUT)

/** 仅将状态改为 toClear */
fun Task.toClear() = copy(state = TaskState.CLEAR)