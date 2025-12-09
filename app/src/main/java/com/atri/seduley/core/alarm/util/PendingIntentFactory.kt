package com.atri.seduley.core.alarm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.atri.seduley.domain.alarm.AlarmReceiver

object PendingIntentFactory {

    fun createBroadcast(
        context: Context,
        requestCode: Int
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION
            putExtra("requestCode", requestCode)
        }

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
