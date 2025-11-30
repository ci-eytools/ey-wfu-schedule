package com.atri.seduley.core.alarm.impl

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import com.atri.seduley.core.alarm.AlarmBackend
import com.atri.seduley.core.alarm.AlarmConfig
import com.atri.seduley.core.alarm.AlarmService
import com.atri.seduley.core.alarm.util.ACTION
import com.atri.seduley.core.alarm.util.PendingIntentFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject

class AlarmServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : AlarmService {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /**
     * 设置定时闹钟
     *
     * INEXACT_ALARM 模式交由系统自动重复
     * EXACT_ALARM 模式需手动重设
     */
    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(config: AlarmConfig) {
        val triggerMillis = config.triggerAt
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val pendingIntent = PendingIntentFactory.createBroadcast(
            context = context,
            requestCode = config.requestCode,
            action = ACTION
        )

        when (config.backend) {

            AlarmBackend.INEXACT_ALARM -> {
                // 非精确闹钟，每日重复
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    AlarmManager.INTERVAL_DAY,
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
            requestCode = requestCode,
            action = ACTION
        )

        alarmManager.cancel(pi)
    }
}
