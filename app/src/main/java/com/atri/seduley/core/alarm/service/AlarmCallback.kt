package com.atri.seduley.core.alarm.service

import android.Manifest
import android.util.Log
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.notification.notifier.CustomCardNotifier
import com.atri.seduley.core.notification.notifier.SystemBarNotification
import javax.inject.Inject

interface AlarmCallback {
    fun onAlarmTriggered(alarm: Alarm)
}

class SystemNotificationCallback @Inject constructor(
    private val systemBarNotification: SystemBarNotification
) : AlarmCallback {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onAlarmTriggered(alarm: Alarm) {
        Log.d("MyAlarm", "SystemNotificationCallback onAlarmTriggered: $alarm")
        val msg = when (alarm) {
            is MessageAlarm -> alarm.message
            else -> ""
        }
        systemBarNotification.show(alarm.title, msg)
    }
}

class CustomCardCallback @Inject constructor(
    private val customCardNotifier: CustomCardNotifier
) : AlarmCallback {
    override fun onAlarmTriggered(alarm: Alarm) {
        val notifier = customCardNotifier.apply {
            addActionButton("稍后") { /* 延迟逻辑 */ }
            addActionButton("关闭") { /* 取消逻辑 */ }
        }
        notifier.show(alarm.title, "消息闹钟触发")
    }
}