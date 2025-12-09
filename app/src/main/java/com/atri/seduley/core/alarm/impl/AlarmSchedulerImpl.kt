package com.atri.seduley.core.alarm.impl

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import com.atri.seduley.core.alarm.AlarmBackend
import com.atri.seduley.core.alarm.AlarmConfig
import com.atri.seduley.core.alarm.AlarmScheduler
import com.atri.seduley.core.alarm.util.PendingIntentFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
    private val alarmManager: AlarmManager,
    @ApplicationContext private val context: Context
) : AlarmScheduler {

    /** 设置定时闹钟 */
    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(config: AlarmConfig) {
        val triggerMillis = config.triggerAt
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val pendingIntent = PendingIntentFactory.createBroadcast(
            context = context,
            requestCode = config.requestCode
        )

        when (config.backend) {
            AlarmBackend.INEXACT_ALARM -> {
                // 非精确闹钟
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    config.windowMillis ?: (15 * 60 * 1000L),   // 默认 15min 窗口
                    pendingIntent
                )
            }

            AlarmBackend.EXACT_ALARM -> {
                // 精确闹钟
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent
                )
            }
        }
    }

    /** 取消定时闹钟 */
    override fun cancel(requestCode: Int) {
        val pi = PendingIntentFactory.createBroadcast(
            context = context,
            requestCode = requestCode
        )

        alarmManager.cancel(pi)
    }
}
