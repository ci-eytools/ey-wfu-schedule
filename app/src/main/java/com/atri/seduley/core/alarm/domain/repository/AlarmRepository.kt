package com.atri.seduley.core.alarm.domain.repository

import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmType
import kotlinx.coroutines.flow.Flow

/**
 * 闹钟仓库接口
 * 定义对闹钟数据的增删改查操作
 */
interface AlarmRepository {

    suspend fun getAlarmById(id: Long): Alarm?

    suspend fun addAlarm(alarm: Alarm)

    suspend fun updateAlarm(alarm: Alarm)

    suspend fun deleteAlarm(alarm: Alarm)

    fun getAllAlarms(): Flow<List<Alarm>>

    fun getAlarmsByType(type: AlarmType): Flow<List<Alarm>>
}