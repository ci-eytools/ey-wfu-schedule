package com.atri.seduley.data.local.datastore.entity

import java.time.LocalDateTime

/**
 * 系统设置信息
 */
data class SystemConfInfo(

    /** 是否需要每日课程提醒 */
    val isNeedNotification: Boolean,

    /** 是否需要每日更新课程 */
    val isNeedUpdateCourse: Boolean,

    /** 最后更新课表的日期 */
    val lastUpdatedCourseDate: LocalDateTime
)