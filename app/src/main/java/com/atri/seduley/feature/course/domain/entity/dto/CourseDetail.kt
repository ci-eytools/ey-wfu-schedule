package com.atri.seduley.feature.course.domain.entity.dto

data class CourseDetail(
    val courseName: String,
    val dayOfWeek: Int,
    val section: Int,
    val location: String
)