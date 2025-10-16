package com.atri.seduley.core.alarm.service

import android.util.Log
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmState
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import com.atri.seduley.core.alarm.util.PendingIntentFactory
import javax.inject.Inject

class AlarmManagerService @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) {

    suspend fun addAlarm(alarm: Alarm) {
        Log.d("MyAlarm", "addAlarm: $alarm")
        repository.addAlarm(alarm)
        val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
        scheduler.scheduleAlarm(alarm, pi)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        repository.updateAlarm(alarm)
        when (alarm.state) {
            AlarmState.AWAIT -> {
                val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
                scheduler.scheduleAlarm(alarm, pi)
            }

            else -> {
                val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
                scheduler.cancelAlarm(pi)
            }
        }
    }

    suspend fun deleteAlarm(alarm: Alarm) {
        repository.deleteAlarm(alarm)
        val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
        scheduler.cancelAlarm(pi)
    }

    suspend fun pauseAlarm(alarm: Alarm) {
        PendingIntentFactory.cancelPendingIntent(scheduler.context, alarm)
        repository.updateAlarm(alarm.copy(state = AlarmState.PAUSED))
    }

    suspend fun restartAlarm(alarm: Alarm) {
        if (alarm.state == AlarmState.PAUSED) {
            this.addAlarm(alarm)
        }
    }

    fun checkAlarmExists(alarm: Alarm): Boolean {
        return scheduler.isAlarmSet(alarm)
    }

    fun getAllAlarms() = repository.getAllAlarms()

    fun getAlarmsByType(type: AlarmType) = repository.getAlarmsByType(type)
}