package com.atri.seduley.domain.model

import java.time.LocalDateTime

data class SystemConf(

    /** 是否需要每日课程提醒 */
    val isNeedNotification: Boolean,

    /** 是否需要每日更新课程 */
    val isNeedUpdateCourse: Boolean,

    /** 最后更新课表的日期 */
    val lastUpdatedCourseDate: LocalDateTime
)