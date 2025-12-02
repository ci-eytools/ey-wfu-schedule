package com.atri.seduley.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Student(
    val studentId: Long,
    val semester: Semester,
    val nickName: String,
    val courseUpdatedAt: LocalDateTime,
    val params: Map<String, String>
)

data class Semester(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalWeeks: Int
)