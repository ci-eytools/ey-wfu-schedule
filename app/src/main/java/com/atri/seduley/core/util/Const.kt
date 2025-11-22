package com.atri.seduley.core.util

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 定义常量
 */
object Const {

    /** 封面图片名 */
    const val COVER_IMAGE_NAME = "cover.jpg"

    /** 开屏页图片名 */
    const val SPLASH_IMAGE_NAME = "splash.jpg"

    /** 每日课程提醒触发时间 */
    val DAILY_CLAZZ_NOTIFICATION_TIME: LocalTime = LocalTime.of(22, 0)

    /**
     * 每日课程自动更新时间
     */
    val DAILY_UPDATE_COURSE_TIME: LocalTime = LocalTime.of(22, 0)

    /** 删除大于此数量的闹钟 */
    const val DELECT_ALARM_NUM: Int = 1000

    /** 当今日无课时切换到明日课表的时间 */
    val SWITCH_SELECTED_DATE_TOMORROW: LocalTime = LocalTime.of(18, 0)

    val NO_LAST_UPDATE_SELECTED_DATE: LocalDateTime =
        LocalDateTime.of(2020, 8, 31, 0, 0)
}