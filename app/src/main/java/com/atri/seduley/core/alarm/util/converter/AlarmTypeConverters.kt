package com.atri.seduley.core.alarm.util.converter

import androidx.room.TypeConverter
import com.atri.seduley.core.alarm.domain.model.AlarmState
import com.atri.seduley.core.alarm.domain.model.AlarmType

class AlarmEnumConverters {

    @TypeConverter
    fun fromAlarmType(type: AlarmType): Int = type.value

    @TypeConverter
    fun toAlarmType(value: Int): AlarmType = AlarmType.fromInt(value)

    @TypeConverter
    fun fromAlarmState(state: AlarmState): Int = state.value

    @TypeConverter
    fun toAlarmState(value: Int): AlarmState = AlarmState.fromInt(value)
}
