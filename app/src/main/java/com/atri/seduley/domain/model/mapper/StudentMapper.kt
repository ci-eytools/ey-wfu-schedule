package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity
import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.model.Student

/** StudentEntity -> Student */
fun StudentEntity.toDomain() =
    Student(
        studentId = 0,
        semester = semester.toDomain(),
        updatedAt = courseUpdatedAt
    )

/** Student -> StudentEntity */
fun Student.toEntity() =
    StudentEntity(
        studentId = 0,
        semester = semester.toEntity(),
        courseUpdatedAt = updatedAt
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