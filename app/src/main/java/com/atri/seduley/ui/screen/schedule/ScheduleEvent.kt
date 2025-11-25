package com.atri.seduley.ui.screen.schedule

import com.atri.seduley.domain.model.Course
import com.atri.seduley.ui.screen.schedule.components.SwitchWeekWay
import java.time.LocalDate

/**
 * 课表事件
 */
sealed class ScheduleEvent {

    /** 切换日期 */
    data class SwitchDate(val date: LocalDate) : ScheduleEvent()

    /** 选择课程（留有事件，暂无效） */
    data class SelectCourse(val course: Course) : ScheduleEvent()

    /** 切换周 */
    data class SwitchWeek(val where: SwitchWeekWay): ScheduleEvent()
}