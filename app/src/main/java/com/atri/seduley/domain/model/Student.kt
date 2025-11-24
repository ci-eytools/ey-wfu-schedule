package com.atri.seduley.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Student(
    val studentId: String = "",  // 唯一索引
    val semester: Semester = Semester(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class Semester(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val totalWeeks: Int = 0
)