package com.atri.seduley.core.alarm.domain.model

enum class AlarmType(val value: Int) {
    SCHEDULED(0),
    MESSAGE(1);

    companion object {
        private val map = AlarmType.entries.associateBy(AlarmType::value)
        fun fromInt(value: Int) = map[value] ?: SCHEDULED
    }
}