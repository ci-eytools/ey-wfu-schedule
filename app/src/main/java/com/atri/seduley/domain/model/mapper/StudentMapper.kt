package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity
import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.model.Student

/** StudentEntity -> Student */
fun StudentEntity.toDomain() =
    Student(
        studentId = studentId,
        semester = semester.toDomain(),
        nickName = nickName ?: "",
        courseUpdatedAt = courseUpdatedAt,
        params = params ?: emptyMap()
    )

/** Student -> StudentEntity */
fun Student.toEntity() =
    StudentEntity(
        studentId = studentId,
        semester = semester.toEntity(),
        nickName = nickName,
        courseUpdatedAt = courseUpdatedAt,
        params = params
    )

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