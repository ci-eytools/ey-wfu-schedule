package com.atri.seduley.domain.model.mapper

import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.domain.model.Course

/** CourseEntity -> Course */
fun CourseEntity.toDomain(): Course =
    Course(
        name = name,
        credit = credit,
        type = type,
        location = location,
        date = date,
        weekly = weekly,
        dayOfWeek = dayOfWeek,
        section = section
    )

/** Course → CourseEntity */
fun Course.toEntity(studentId: Long): CourseEntity =
    CourseEntity(
        id = 0,
        studentId = studentId,
        name = name,
        credit = credit,
        type = type,
        location = location,
        date = date,
        weekly = weekly,
        dayOfWeek = dayOfWeek,
        section = section
    )
