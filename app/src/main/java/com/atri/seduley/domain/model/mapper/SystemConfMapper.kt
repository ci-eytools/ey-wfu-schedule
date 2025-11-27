package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.domain.model.SystemConf
import java.time.LocalDateTime

/** SystemConfEntity -> SystemConf */
fun SystemConfEntity.toDomain(): SystemConf =
    SystemConf(
        isNeedNotification = isNeedNotification,
        isNeedUpdateCourse = isNeedUpdateCourse,
        lastUpdatedCourseDate = lastUpdatedCourseDate
    )

/** SystemConf → SystemConfEntity */
fun SystemConf.toEntity(): SystemConfEntity =
    SystemConfEntity(
        isNeedNotification = isNeedNotification,
        isNeedUpdateCourse = isNeedUpdateCourse,
        lastUpdatedCourseDate = LocalDateTime.now()
    )