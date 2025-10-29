package com.atri.seduley.core.notification.notifier

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.atri.seduley.R
import com.atri.seduley.SplashActivity
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.notification.channel.NotificationChannelManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 系统通知栏通知
 */
class SystemBarNotification @Inject constructor(
    @ApplicationContext private val context: Context,
    notificationChannelManager: NotificationChannelManager
) : AlarmNotifier {

    companion object {
        const val CHANNEL_ID = "scheduled_alarm_channel"
        const val CHANNEL_NAME = "Seduley"
    }

    init {
        AppLogger.d("SystemBarNotification init")
        // 创建通知通道
        notificationChannelManager.createChannel(
            CHANNEL_ID, CHANNEL_NAME, importance = android.app.NotificationManager.IMPORTANCE_HIGH
        )
    }

    /**
     * 展示系统通知
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun show(title: String, message: String) {
        AppLogger.d("SystemBarNotification show: $title, $message")

        val intent = Intent(context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context)
            .notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
