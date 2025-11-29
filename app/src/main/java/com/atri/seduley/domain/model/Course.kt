package com.atri.seduley.domain.model

import java.time.LocalDate

data class Course(
    val name: String,
    val credit: Int,        // * 100 存 int
    val type: String,
    val location: String,
    val date: LocalDate,
    val weekly: Int,        // 周次
    val dayOfWeek: Int,
    val section: Int        // 第几大节
)