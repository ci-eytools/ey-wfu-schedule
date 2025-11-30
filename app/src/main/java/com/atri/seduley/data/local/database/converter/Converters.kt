package com.atri.seduley.data.local.database.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? =
        value?.atZone(ZoneId.of("UTC"))?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? =
        value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.of("UTC")).toLocalDateTime() }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? =
        value?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? =
        value?.let { LocalDate.ofEpochDay(it) }
}
