package com.atri.seduley.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "students",
    indices = [Index(value = ["studentId"], unique = true)]
)
data class StudentEntity(
    @PrimaryKey val studentId: Long,
    @Embedded val semester: SemesterEntity,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class SemesterEntity(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalWeeks: Int
)