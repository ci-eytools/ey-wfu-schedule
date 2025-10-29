package com.atri.seduley.core.alarm.domain.repository

import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmType

/**
 * 闹钟仓库接口
 * 定义对闹钟数据的增删改查操作
 */
interface AlarmRepository {

    suspend fun getAlarmById(id: Long): Alarm?

    suspend fun addAlarm(alarm: Alarm)

    suspend fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    suspend fun restartAlarm(alarm: Alarm)

    suspend fun getAllAlarms(): List<Alarm>

    suspend fun getAllAlarmsCount(): Int

    suspend fun getAlarmsByType(type: AlarmType): List<Alarm>

    suspend fun deleteCompletedScheduledAlarms(): Int

    suspend fun deleteAlarmsOlderThan(day: Int): Pair<Int, Int>

    suspend fun deleteAllAlarms(): Int
}