package com.atri.seduley.core.alarm.domain.model

/**
 * 触发模式, 用于描述闹钟的触发方式, 例如: 精确触发, 不精确触发, Job, Work等
 */
enum class TriggerMode(val value: Int) {
    UNKNOWN_ALARM(-1),
    EXACT_ALARM(0),
    INACCURATE_ALARM(1),
    DAILY_ALARM(2),
    JOB(3),
    WORK(4);

    companion object {
        private val map = TriggerMode.entries.associateBy(TriggerMode::value)
        fun fromInt(value: Int) = map[value] ?: UNKNOWN_ALARM
    }
}