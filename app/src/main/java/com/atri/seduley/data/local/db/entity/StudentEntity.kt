package com.atri.seduley.data.local.db.entity

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
class StudentEntity {
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0
    val studentId: String = ""  // 唯一索引
    val courses: List<Course> = emptyList()
    val semester: Semester = Semester()
    val createdAt: LocalDateTime = LocalDateTime.now()
    val updatedAt: LocalDateTime = LocalDateTime.now()
}

@Serializable
class Course {
    val name: String = ""
    val credit: Int = 0       // * 100 存 int
    val type: String = ""
    val location: String = ""
    val date: Long = 0
    val weekly: Int = 0
    val dayOfWeek: Int = 0
    val section: Int = 0      // 位掩码
}

@Serializable
class Semester {
    val startDate: LocalDate = LocalDate.now()
    val endDate: LocalDate = LocalDate.now()
    val totalWeeks: Int = 0
}