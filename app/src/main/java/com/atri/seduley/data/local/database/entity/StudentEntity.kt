package com.atri.seduley.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "students",
    indices = [Index(value = ["studentId"], unique = true)]
)
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: String = "",  // 唯一索引
    val courses: List<Course> = emptyList(),
    val semester: Semester = Semester(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

@Serializable
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

@Serializable
data class Semester(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val totalWeeks: Int = 0
)