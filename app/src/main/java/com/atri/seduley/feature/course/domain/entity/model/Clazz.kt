package com.atri.seduley.feature.course.domain.entity.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    // 约束去重
    indices = [Index(value = ["date", "section"], unique = true)]
)
data class Clazz(
    @PrimaryKey val id: Long,
    val courseId: Long,         // 课程 ID
    val weekly: Int,            // 周次
    val dayOfWeek: Int,         // 星期
    val section: Int,           // 小节(位掩码)
    val date: Long,             // 日期(时间戳)
    val location: String        // 上课地点
)