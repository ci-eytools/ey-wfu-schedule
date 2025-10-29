package com.atri.seduley.core.alarm.domain.model

import org.threeten.bp.LocalDateTime

/**
 * 基础闹钟类（密封类）
 * 定义通用属性，如 id、计划时间、状态等
 */
sealed class Alarm(
    open val id: Long,
    open val requestCode: Int,
    open val title: String,
    open val time: LocalDateTime,
    open val type: AlarmType,
    open val triggerMode: TriggerMode,
    open val state: AlarmState
) {
    abstract fun copy(
        id: Long = this.id,
        requestCode: Int = this.requestCode,
        title: String = this.title,
        time: LocalDateTime = this.time,
        type: AlarmType = this.type,
        triggerMode: TriggerMode = this.triggerMode,
        state: AlarmState = this.state
    ): Alarm
}
