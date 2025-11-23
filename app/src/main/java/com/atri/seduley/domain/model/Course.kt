package com.atri.seduley.domain.model

import java.time.LocalDate

data class Course(
    val name: String = "",
    val credit: Int = 0,        // * 100 存 int
    val type: String = "",
    val location: String = "",
    val date: LocalDate = LocalDate.now(),
    val weekly: Int = 0,        // 周次
    val dayOfWeek: Int = 0,
    val section: Int = 0        // 第几大节
)