package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.StudentEntity
import com.atri.seduley.domain.model.Student

/** StudentEntity -> Student */
fun StudentEntity.toDomain() =
    Student(
        studentId = studentId,
        semester = semester,
        updatedAt = updatedAt
    )

/** Student -> StudentEntity */
fun Student.toEntity() =
    StudentEntity(
        studentId = studentId,
        semester = semester,
        updatedAt = updatedAt
    )