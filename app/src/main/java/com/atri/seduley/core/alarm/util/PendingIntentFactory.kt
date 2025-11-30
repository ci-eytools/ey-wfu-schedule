package com.atri.seduley.core.alarm.util

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object PendingIntentFactory {

    fun createBroadcast(
        context: Context,
        requestCode: Int,
        action: String
    ): PendingIntent {
        val intent = Intent(action)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
