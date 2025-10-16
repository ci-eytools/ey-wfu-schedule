package com.atri.seduley.core.alarm

import android.util.Log
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import com.atri.seduley.core.alarm.service.AlarmManagerService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 对外暴露的闹钟管理接口
 * 支持创建 / 修改 / 删除 / 暂停 / 查询
 * 支持不同闹钟回调，可自定义通知行为
 */
class AlarmApi @Inject constructor(
    private val service: AlarmManagerService
) {
    suspend fun createAlarm(alarm: Alarm) {
        Log.d("MyAlarm", "createAlarm: $alarm")
        service.addAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) = service.updateAlarm(alarm)

    suspend fun deleteAlarm(alarm: Alarm) = service.deleteAlarm(alarm)

    suspend fun pauseAlarm(alarm: Alarm) = service.pauseAlarm(alarm)

    suspend fun restartAlarm(alarm: Alarm) = service.restartAlarm(alarm)

    fun getAllAlarms(): Flow<List<Alarm>> = service.getAllAlarms()

    fun getAlarmsByType(type: AlarmType): Flow<List<Alarm>> = service.getAlarmsByType(type)

    fun checkExists(alarm: Alarm): Boolean = when (alarm) {
        is ScheduledAlarm -> service.checkAlarmExists(alarm)
        else -> false
    }
}