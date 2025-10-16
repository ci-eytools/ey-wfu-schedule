package com.atri.seduley.core.alarm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
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
        Log.d("MyAlarm", "createPendingIntent: $alarm")
        return PendingIntent.getBroadcast(
            context,
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun cancelPendingIntent(context: Context, alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pi = PendingIntent.getBroadcast(
            context,
            alarm.requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pi?.cancel()
    }
}