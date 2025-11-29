package com.atri.seduley.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "courses",
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["date"]),
    ]
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val studentId: Long,
    val name: String,
    val credit: Int,        // * 100 存 int
    val type: String,
    val location: String,
    val date: LocalDate,
    val weekly: Int,        // 周次
    val dayOfWeek: Int,
    val section: Int        // 第几大节
)