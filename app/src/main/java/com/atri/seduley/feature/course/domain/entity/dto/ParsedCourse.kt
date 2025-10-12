package com.atri.seduley.feature.course.domain.entity.dto

data class ParsedCourse(
    val name: String,
    val credit: Int,
    val type: String,
    val location: String,
    val date: Long,
    val weekly: Int,
    val dayOfWeek: Int,
    val section: Int
)


