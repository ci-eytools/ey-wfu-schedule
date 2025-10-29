package com.atri.seduley.core.alarm.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.TriggerMode
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.ZoneId
import java.util.Calendar
import javax.inject.Inject

/**
 * 调度器负责与系统 AlarmManager 交互
 */
class AlarmScheduler @Inject constructor(
    @ApplicationContext val context: Context
) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * 设置闹钟
     */
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarm(alarm: Alarm, pendingIntent: PendingIntent) {
        val triggerAtMillis = alarm.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        when(alarm.triggerMode) {

            TriggerMode.UNKNOWN_ALARM -> {}

            TriggerMode.EXACT_ALARM -> {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
            TriggerMode.INACCURATE_ALARM -> {}

            TriggerMode.DAILY_ALARM -> {
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, alarm.time.hour)
                }
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }

            TriggerMode.JOB -> {}

            TriggerMode.WORK -> {}
        }
    }

    /**
     * 取消闹钟
     */
    fun cancelAlarm(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }
}