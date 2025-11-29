package com.atri.seduley.domain.model

import java.time.LocalDate

data class Semester(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val totalWeeks: Int = 0
)