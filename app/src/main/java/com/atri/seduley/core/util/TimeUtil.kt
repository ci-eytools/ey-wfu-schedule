package com.atri.seduley.core.util

import com.atri.seduley.core.exception.BaseException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.concurrent.TimeUnit

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
     * 获取 传入时间 与 当前时间 相减的时间戳, 负数时间戳转为 0
     */
    fun safeDelayMillis(triggerTime: LocalDateTime): Long {
        val delay = ChronoUnit.MILLIS.between(LocalDateTime.now(), triggerTime)
        return maxOf(delay, 0)
    }

    /**
     * 将指定的时间单位与目标值转换为毫秒
     *
     * @param timeUnit 时间单位
     * @param num 对应的时间数量
     * @return 对应的毫秒值
     */
    fun getMillisByTimeUnit(timeUnit: TimeUnit, num: Int): Long {
        return when (timeUnit) {
            TimeUnit.DAYS -> TimeUnit.DAYS.toMillis(num.toLong())
            TimeUnit.HOURS -> TimeUnit.HOURS.toMillis(num.toLong())
            TimeUnit.MINUTES -> TimeUnit.MINUTES.toMillis(num.toLong())
            TimeUnit.SECONDS -> TimeUnit.SECONDS.toMillis(num.toLong())
            else -> {
                throw BaseException("毫秒以下单位不支持转换")
            }
        }
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

    fun fromTimestampToLocalDateTime(value: Long?): LocalDateTime? {
        if (value == null) return null
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

    fun localDateTimeToTimestamp(dateTime: LocalDateTime?): Long? {
        if (dateTime == null) return null
        return dateTime.atZone(zoneId).toInstant().toEpochMilli()
    }

    /**
     * 计算位掩码周次信息, 舍弃第 0 位
     */
    fun calculateWeeks(weeks: Int = 0, weekly: Int): Int {
        return weeks or (1 shl weekly)
    }

    /**
     * 将 LocalTime 转换为 LocalDateTime
     */
    fun localTimeToLocalDateTime(time: LocalTime): LocalDateTime {
        return LocalDateTime.of(LocalDate.now(), time)
    }
}
