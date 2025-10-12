package com.atri.seduley.feature.course.presentation.daily

import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import com.atri.seduley.feature.course.presentation.daily.components.SwitchWeekWay
import org.threeten.bp.LocalDate

sealed class DailyScheduleEvent {
    class SwitchDate(val date: LocalDate) : DailyScheduleEvent()
    class SelectCourse(val course: CourseDetail) : DailyScheduleEvent()
    class SwitchWeek(val where: SwitchWeekWay): DailyScheduleEvent()
}