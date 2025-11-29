package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.domain.model.Semester

/** SemesterEntity -> Semester */
fun SemesterEntity.toDomain() =
    Semester(
        startDate = startDate,
        endDate = endDate,
        totalWeeks = totalWeeks
    )

/** Semester -> SemesterEntity */
fun Semester.toEntity() =
    SemesterEntity(
        startDate = startDate,
        endDate = endDate,
        totalWeeks = totalWeeks
    )