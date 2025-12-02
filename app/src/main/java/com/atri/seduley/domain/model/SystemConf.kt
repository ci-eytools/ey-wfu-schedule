package com.atri.seduley.domain.model

import com.atri.seduley.data.local.datastore.entity.TaskWay

data class SystemConf(

    /** 是否需要每日课程提醒 */
    val notificationWay: TaskWay,

    /** 是否需要每日更新课程 */
    val updateCourseWay: TaskWay
)