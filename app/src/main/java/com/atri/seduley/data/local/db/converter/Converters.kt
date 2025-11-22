package com.atri.seduley.data.local.db.converter

import androidx.room.TypeConverter
import com.atri.seduley.data.local.db.entity.Course
import com.atri.seduley.data.local.db.entity.Semester
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
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
    fun fromCourses(courses: List<Course>): String = Json.encodeToString(courses)

    @TypeConverter
    fun toCourses(value: String): List<Course> = Json.decodeFromString<List<Course>>(value)

    @TypeConverter
    fun fromSemester(semester: Semester): String = Json.encodeToString(semester)

    @TypeConverter
    fun toSemester(value: String): Semester = Json.decodeFromString<Semester>(value)
}
