package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.domain.model.SystemConf
import java.time.LocalDateTime

/** SystemConfEntity -> SystemConf */
fun SystemConfEntity.toDomain(): SystemConf =
    SystemConf(
        seedColor = seedColor,
        isNeedNotification = isNeedNotification,
        isNeedUpdateCourse = isNeedUpdateCourse,
        lastUpdatedCourseDate = lastUpdatedCourseDate
    )

/** SystemConf → SystemConfEntity */
fun SystemConf.toEntity(): SystemConfEntity =
    SystemConfEntity(
        seedColor = seedColor,
        isNeedNotification = isNeedNotification,
        isNeedUpdateCourse = isNeedUpdateCourse,
        lastUpdatedCourseDate = LocalDateTime.now()
    )