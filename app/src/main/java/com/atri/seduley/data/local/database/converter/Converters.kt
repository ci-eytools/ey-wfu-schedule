package com.atri.seduley.data.local.database.converter

import androidx.room.TypeConverter
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.data.local.database.entity.TriggerMode
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

object Converters {

    private val json = Json { encodeDefaults = false; ignoreUnknownKeys = true }
    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): Long? = value?.atZone(
        ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun toLocalDateTime(value: Long?): LocalDateTime? = value?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime() }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? =
        value?.toEpochDay()

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? =
        value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun fromTriggerMode(value: TriggerMode?): Int? = value?.value

    @TypeConverter
    fun toTriggerMode(value: Int?): TriggerMode? = value?.let {
        TriggerMode.entries.firstOrNull { it.value == value } }

    @TypeConverter
    fun fromCallback(value: Callback?): Int? = value?.value

    @TypeConverter
    fun toCallback(value: Int?): Callback? = value?.let { Callback.entries.firstOrNull { it.value == value } }

    @TypeConverter
    fun fromTaskState(value: TaskState?): Int? = value?.value

    @TypeConverter
    fun toTaskState(value: Int?): TaskState? = value?.let { TaskState.entries.firstOrNull { it.value == value } }

    @TypeConverter
    fun fromMap(value: Map<String, String>?): String? = value?.let { json.encodeToString(mapSerializer, it) }

    @TypeConverter
    fun toMap(value: String?): Map<String, String>? = value?.let { json.decodeFromString(mapSerializer, it) }
}
