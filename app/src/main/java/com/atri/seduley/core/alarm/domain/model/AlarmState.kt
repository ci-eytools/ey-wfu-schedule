package com.atri.seduley.core.alarm.domain.model

/**
 * 闹钟状态
 */
enum class AlarmState(val value: Int) {
    AWAIT(0),
    PAUSED(1),
    FIRE(2),
    DONE(3),
    TIME_OUT(4);

    companion object {
        private val map = AlarmState.entries.associateBy(AlarmState::value)
        fun fromInt(value: Int) = map[value] ?: AWAIT
    }
}