package com.atri.seduley.core.util

import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.Calendar

object TimeUtil {

    private val zoneId: ZoneId = ZoneId.systemDefault()

    /**
     * 获取当年指定 mouth, day 的时间戳
     */
    fun getTimestamp(mouth: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        calendar.set(year, mouth - 1, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 获取当前时间戳所在周
     */
    fun getNowWeekly(startDate: Long): Int {
        val currentData = Calendar.getInstance()
        val diffMillis = currentData.timeInMillis - startDate
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return diffDays / 7 + 1 // 从第一周开始
    }

    /**
     * 获取指定日期所在周
     */
    fun getWeekly(startDate: LocalDate, targetDate: LocalDate): Int {
        val diffMillis = localDateToTimestamp(targetDate) - localDateToTimestamp(startDate)
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
        return diffDays / 7 + 1 // 从第一周开始
    }

    /**
     * 将时间戳转换为 LocalDate
     */
    fun fromTimestampToLocalDate(value: Long): LocalDate {
        return value.let {
            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDate()
        }
    }

    /**
     * 将 LocalDate 转换为时间戳
     */
    fun localDateToTimestamp(date: LocalDate): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    /**
     * 将时间戳转换为 LocalDateTime
     */
    fun fromTimestampToLocalDateTime(value: Long): LocalDateTime {
        return value.let {
            Instant.ofEpochMilli(it).atZone(zoneId).toLocalDateTime()
        }
    }

    /**
     * 将 LocalDateTime 转换为时间戳
     */
    fun localDateTimeToTimestamp(dateTime: LocalDateTime): Long {
        return dateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    /**
     * 计算位掩码周次信息, 舍弃第 0 位
     */
    fun calculateWeeks(weeks: Int = 0, weekly: Int): Int {
        return weeks or (1 shl weekly)
    }
}
