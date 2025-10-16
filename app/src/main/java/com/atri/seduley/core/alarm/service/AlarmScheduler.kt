package com.atri.seduley.core.alarm.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.ZoneId
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
        Log.d("MyAlarm", "scheduleAlarm: $alarm")
        val triggerAtMillis = alarm.time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        when (alarm) {
            is ScheduledAlarm -> {
                if (alarm.allowInexact) {
                    // 容忍不精确，可被省电模式推迟
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                } else {
                    // 精确闹钟
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            }

            is MessageAlarm -> {
                Log.d("MyAlarm", "scheduleAlarm: $alarm")
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }

    }

    /**
     * 取消闹钟
     */
    fun cancelAlarm(pendingIntent: PendingIntent) {
        alarmManager.cancel(pendingIntent)
    }

    /**
     * 检查闹钟是否存在（通过 PendingIntent）
     */
    fun isAlarmSet(alarm: Alarm): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) != null
    }
}