package com.atri.seduley.core.notification.notifier

/**
 * 通知接口，定义所有通知统一行为
 */
interface AlarmNotifier {
    fun show(title: String, message: String)
}
