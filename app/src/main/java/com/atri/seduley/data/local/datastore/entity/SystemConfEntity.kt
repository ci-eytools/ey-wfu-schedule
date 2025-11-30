package com.atri.seduley.data.local.datastore.entity

/**
 * 系统设置信息
 */
data class SystemConfEntity(

    /** 是否需要每日课程提醒 */
    val isNeedNotification: Boolean,

    /** 是否需要每日更新课程 */
    val isNeedUpdateCourse: Boolean
)