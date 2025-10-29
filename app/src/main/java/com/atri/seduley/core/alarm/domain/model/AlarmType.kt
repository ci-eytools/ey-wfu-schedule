package com.atri.seduley.core.alarm.domain.model

/**
 * 闹钟类型
 */
enum class AlarmType(val value: Int) {
    NO_ALARM(-1),
    SCHEDULED(0),
    MESSAGE(1),
    DAILY_CLAZZ_NOTIFICATION(2),
    DAILY_UPDATE_SCHEDULE(3);

    companion object {
        private val map = AlarmType.entries.associateBy(AlarmType::value)
        fun fromInt(value: Int) = map[value] ?: NO_ALARM
    }
}