package com.atri.seduley.core.alarm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.service.AlarmReceiver

/**
 * 生成闹钟 PendingIntent
 */
object PendingIntentFactory {

    fun createPendingIntent(context: Context, alarm: Alarm): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
        }
        AppLogger.d("createPendingIntent: $alarm")
        return PendingIntent.getBroadcast(
            context,
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}