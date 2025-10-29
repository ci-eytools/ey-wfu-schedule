package com.atri.seduley.core.alarm.data.repository

import com.atri.seduley.core.alarm.data.AlarmDao
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmState
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.util.TimeUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.concurrent.TimeUnit

/**
 * AlarmRepository 的实现类
 */
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
            is ScheduledAlarm -> alarmDao.updateScheduleAlarm(alarm)
            is MessageAlarm -> alarmDao.updateMessageAlarm(alarm)
        }
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        when (alarm) {
            is ScheduledAlarm -> alarmDao.deleteScheduledAlarm(alarm)
            is MessageAlarm -> alarmDao.deleteMessageAlarm(alarm)
        }
    }

    override suspend fun restartAlarm(alarm: Alarm) {
        if (alarm.state == AlarmState.PAUSED)
            addAlarm(alarm.copy(state = AlarmState.AWAIT))
    }

    override suspend fun getAllAlarms(): List<Alarm> {
        return alarmDao.getAllScheduledAlarms() +
                alarmDao.getAllMessageAlarms()
    }

    override suspend fun getAllAlarmsCount(): Int {
        return alarmDao.getAllAlarmsCount()
    }

    override suspend fun getAlarmsByType(type: AlarmType): List<Alarm> {
        return when (type) {
            AlarmType.SCHEDULED -> alarmDao.getAllScheduledAlarms()
            AlarmType.MESSAGE -> alarmDao.getAllMessageAlarms()
            else -> {
                throw BaseException("未知的 alarm 类型")
            }
        }
    }

    override suspend fun deleteCompletedScheduledAlarms(): Int {
        return alarmDao.deleteCompletedScheduledAlarms()
    }

    /**
     * 删除传入天数前的数据
     */
    override suspend fun deleteAlarmsOlderThan(day: Int): Pair<Int, Int> {
        return alarmDao.deleteAlarmsOlderThan(TimeUtil.getMillisByTimeUnit(TimeUnit.DAYS, day))
    }

    override suspend fun deleteAllAlarms(): Int {
        return alarmDao.deleteAllAlarmsTransaction().let { (i, j) -> i + j }
    }
}