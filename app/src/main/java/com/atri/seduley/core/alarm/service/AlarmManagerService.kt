package com.atri.seduley.core.alarm.service

import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.AlarmState
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.model.TriggerMode
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import com.atri.seduley.core.alarm.util.PendingIntentFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

/**
 * 闹钟服务
 */
class AlarmManagerService @Inject constructor(
    private val repository: AlarmRepository,
    private val scheduler: AlarmScheduler
) {

    /**
     * 添加闹钟
     */
    suspend fun addAlarm(alarm: Alarm) {
        when (alarm.triggerMode) {
            TriggerMode.UNKNOWN_ALARM -> {}
            TriggerMode.WORK -> {}
            else -> {
                repository.addAlarm(alarm)
                val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
                scheduler.scheduleAlarm(alarm, pi)
            }
        }
    }

    /**
     * 删除闹钟
     */
    suspend fun deleteAlarm(alarm: Alarm) {
        when (alarm.triggerMode) {
            TriggerMode.UNKNOWN_ALARM -> {}
            TriggerMode.WORK -> {}
            else -> {
                repository.getAllAlarms()
                    .filter {
                        it.time == alarm.time
                                && it.type == alarm.type
                                && it.triggerMode == alarm.triggerMode
                    }
                    .forEach {
                        val pi = PendingIntentFactory
                            .createPendingIntent(scheduler.context, it)
                        scheduler.cancelAlarm(pi)
                        repository.deleteAlarm(it)
                    }
            }
        }
    }

    /**
     * 获取所有闹钟
     */
    suspend fun getAllAlarms() = repository.getAllAlarms()

    /**
     * 更新所有闹钟
     */
    suspend fun updateAlarm(alarm: Alarm) = repository.updateAlarm(alarm)

    /**
     * 重启闹钟
     */
    suspend fun restartAlarm(alarm: Alarm) {
        repository.restartAlarm(alarm)
        val pi = PendingIntentFactory.createPendingIntent(scheduler.context, alarm)
        scheduler.scheduleAlarm(alarm, pi)
    }

    /**
     * 通过 [time], [type], [triggerMode] 查找闹钟
     */
    suspend fun findAlarmByTimeOrTypeOrTriggerMode(
        time: LocalDateTime,
        type: AlarmType,
        triggerMode: TriggerMode
    ) = withContext(Dispatchers.IO) {
        repository.getAllAlarms().filter { alarm ->
            alarm.time == time
                    && alarm.type == type
                    && alarm.triggerMode == triggerMode
        }
    }

    /**
     * 删除所有无效闹钟
     */
    suspend fun deleteAllInvalidAlarm() = withContext(Dispatchers.IO) {
        repository.getAllAlarms()
            .filter { alarm ->
                when (alarm.state) {
                    AlarmState.DONE, AlarmState.TIME_OUT -> true
                    else -> {
                        false
                    }
                }
            }
            .forEach { repository.deleteAlarm(it) }
    }
}