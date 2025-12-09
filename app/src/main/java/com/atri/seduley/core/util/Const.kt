package com.atri.seduley.core.util

import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 定义常量
 */
object Const {

    /** 封面图片名 */
    const val COVER_IMAGE_NAME = "cover.jpg"

    // gif 封面图
    const val GIF_COVER_IMAGE_NAME = "cover.gif"

    /** 开屏页图片名 */
    const val SPLASH_IMAGE_NAME = "splash.jpg"

    /** 开屏页默认持续时间 */
    const val DEFAULT_SPLASH_DURATION = 300

    /** 默认主题颜色 */
    const val DEFAULT_SEED_COLOR_INT = 0xFF415F91.toInt()

    /** 每日课程提醒触发时间 */
    val DAILY_COURSE_NOTIFICATION_TIME: LocalTime = LocalTime.of(23, 20)

    /** 每日课程自动更新时间 */
    val DAILY_UPDATE_COURSE_TIME: LocalTime = LocalTime.of(19, 0)

    /** 删除大于此数量的闹钟 */
    const val DELECT_ALARM_NUM: Int = 1000

    /** 当今日无课时切换到明日课表的时间 */
    val SWITCH_SELECTED_DATE_TOMORROW: LocalTime = LocalTime.of(15, 0)

    /** 无最后更新选定日期 */
    val NO_LAST_UPDATE_SELECTED_DATE: LocalDateTime =
        LocalDateTime.of(2020, 8, 31, 0, 0)
}