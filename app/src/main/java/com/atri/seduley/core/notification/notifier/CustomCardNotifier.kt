package com.atri.seduley.core.notification.notifier

class CustomCardNotifier : AlarmNotifier {

    private val actions = mutableListOf<Pair<String, () -> Unit>>()

    override fun show(title: String, message: String) {
        // TODO: Jetpack Compose Dialog / Overlay 显示卡片
        println("CustomCardNotifier: $title -> $message")
        actions.forEach { (label, action) ->
            println("按钮: $label")
        }
    }

    fun addActionButton(label: String, onClick: () -> Unit) {
        actions.add(label to onClick)
    }

    fun clearActions() {
        actions.clear()
    }
}