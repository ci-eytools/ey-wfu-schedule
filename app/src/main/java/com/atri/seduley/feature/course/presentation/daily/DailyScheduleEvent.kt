package com.atri.seduley.feature.course.presentation.daily

import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import com.atri.seduley.feature.course.presentation.daily.components.SwitchWeekWay
import org.threeten.bp.LocalDate

/**
 * 每日课表事件
 */
sealed class DailyScheduleEvent {

    /** 切换日期 */
    class SwitchDate(val date: LocalDate) : DailyScheduleEvent()

    /** 选择课程（留有事件，暂无效） */
    class SelectCourse(val course: CourseDetail) : DailyScheduleEvent()

    /** 切换周 */
    class SwitchWeek(val where: SwitchWeekWay): DailyScheduleEvent()
}