package com.atri.seduley.core.alarm.data.repository

import com.atri.seduley.core.alarm.data.AlarmDao
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AlarmRepositoryImpl(
    val alarmDao: AlarmDao
) : AlarmRepository {

    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getScheduledAlarmById(id).takeIf { it != null }
            ?: alarmDao.getMessageAlarmById(id)
    }

    override suspend fun addAlarm(alarm: Alarm) {
        when (alarm) {
            is ScheduledAlarm -> alarmDao.insertScheduledAlarm(alarm)
            is MessageAlarm -> alarmDao.insertMessageAlarm(alarm)
        }
    }

    override suspend fun updateAlarm(alarm: Alarm) {
        when (alarm) {
            is ScheduledAlarm -> alarmDao.insertScheduledAlarm(alarm)
            is MessageAlarm -> alarmDao.insertMessageAlarm(alarm)
        }
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        when (alarm) {
            is ScheduledAlarm -> alarmDao.deleteScheduledAlarm(alarm)
            is MessageAlarm -> alarmDao.deleteMessageAlarm(alarm)
        }
    }

    override fun getAllAlarms(): Flow<List<Alarm>> {
        return combine(
            alarmDao.getAllScheduledAlarms(),
            alarmDao.getAllMessageAlarms()
        ) { scheduledAlarms, messageAlarms ->
            scheduledAlarms + messageAlarms
        }
    }

    override fun getAlarmsByType(type: AlarmType): Flow<List<Alarm>> {
        return when (type) {
            AlarmType.SCHEDULED -> alarmDao.getAllScheduledAlarms()
            AlarmType.MESSAGE -> alarmDao.getAllMessageAlarms()
        }
    }
}