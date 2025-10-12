package com.atri.seduley.feature.course.presentation.daily.util

import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

data class Section(val start: LocalTime, val end: LocalTime)

val sectionMap = mapOf(
    1 to Section(LocalTime.of(8, 0), LocalTime.of(9, 50)),
    2 to Section(LocalTime.of(10, 0), LocalTime.of(12, 0)),
    3 to Section(LocalTime.of(14, 0), LocalTime.of(15, 50)),
    4 to Section(LocalTime.of(16, 0), LocalTime.of(18, 0)),
    5 to Section(LocalTime.of(19, 0), LocalTime.of(20, 50))
)

/**
 * 时间 -> 小节编号
 */
fun timeToSection(time: LocalTime): Int? {
    return sectionMap.entries.find { (_, section) ->
        // 半开区间 [start, end)，上课结束瞬间算下节
        !time.isBefore(section.start) && time.isBefore(section.end.plusSeconds(1))
    }?.key
}

/**
 * 小节编号 -> 时间段
 */
fun sectionToTime(section: Int): Section {
    val section = sectionMap[section]
    return section ?: Section(LocalTime.MIN, LocalTime.MAX)
}

/**
 * 小节编号 -> 时间段(Str)
 */
fun sectionToTimeStr(section: Int): List<String> {
    val sectionTime = sectionMap[section]
    if (sectionTime == null) return listOf("Unknow", "Unknow")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTimeStr = sectionTime.start.format(timeFormatter)
    val endTimeStr = sectionTime.end.format(timeFormatter)
    return listOf(startTimeStr, endTimeStr)
}
