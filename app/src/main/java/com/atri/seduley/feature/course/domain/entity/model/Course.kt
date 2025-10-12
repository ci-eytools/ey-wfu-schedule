package com.atri.seduley.feature.course.domain.entity.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Course(
    @PrimaryKey val id: Long,
    val name: String,
    val credits: Int,   // * 100 存 Int
    val type: String,   // 课程属性
    val weeks: Int      // 总周次信息(位掩码)
)
