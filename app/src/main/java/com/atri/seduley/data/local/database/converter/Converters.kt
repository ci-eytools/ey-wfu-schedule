package com.atri.seduley.data.local.database.converter

import androidx.room.TypeConverter
import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.data.local.database.entity.SemesterEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? =
        value?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? =
        value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? =
        value?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? =
        value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }

    @TypeConverter
    fun fromCourses(courses: List<CourseEntity>): String = json.encodeToString(courses)

    @TypeConverter
    fun toCourses(value: String): List<CourseEntity> = json.decodeFromString<List<CourseEntity>>(value)

    @TypeConverter
    fun fromSemester(semester: SemesterEntity): String = json.encodeToString(semester)

    @TypeConverter
    fun toSemester(value: String): SemesterEntity = json.decodeFromString<SemesterEntity>(value)
}
