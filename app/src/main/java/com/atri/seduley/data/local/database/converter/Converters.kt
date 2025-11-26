package com.atri.seduley.data.local.database.converter

import androidx.room.TypeConverter
import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.data.local.database.entity.SemesterEntity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
    fun fromCourses(courses: List<CourseEntity>): String = json.encodeToString(courses)

    @TypeConverter
    fun toCourses(value: String): List<CourseEntity> = json.decodeFromString<List<CourseEntity>>(value)

    @TypeConverter
    fun fromSemester(semester: SemesterEntity): String = json.encodeToString(semester)

    @TypeConverter
    fun toSemester(value: String): SemesterEntity = json.decodeFromString<SemesterEntity>(value)
}

@OptIn(ExperimentalSerializationApi::class)
object LocalDateSerializer : KSerializer<LocalDate> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.toString()) // ISO 格式：2025-11-26
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString())
    }
}
