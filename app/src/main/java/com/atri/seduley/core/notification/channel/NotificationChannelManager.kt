package com.atri.seduley.core.notification.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.atri.seduley.core.util.AppLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 通知通道管理器（用于创建系统通知通道）
 */
class NotificationChannelManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun createChannel(id: String, name: String, importance: Int) {
        AppLogger.d("createChannel: $id, $name, $importance")
        val channel = NotificationChannel(id, name, importance)
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}
