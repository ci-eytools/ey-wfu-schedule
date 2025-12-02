package com.atri.seduley.data.local.datastore.entity

/**
 * 系统设置信息
 */
data class SystemConfEntity(

    /** 是否需要每日课程提醒 */
    val notificationWay: TaskWay,

    /** 是否需要每日更新课程 */
    val updateCourseWay: TaskWay
)

enum class TaskWay(val value: Int) {
    STOP(0),
    AUTO(1),
    INEXACT_ALARM(2),
    EXACT_ALARM(3);

    companion object {
        fun fromValue(value: Int): TaskWay =
            entries.firstOrNull { it.value == value } ?: STOP
    }
}

fun TaskWay.getMsg() =
    when (this) {
        TaskWay.STOP -> "禁用"
        TaskWay.AUTO -> "自动选择"
        TaskWay.INEXACT_ALARM -> "不精确"
        TaskWay.EXACT_ALARM -> "精确"
    }