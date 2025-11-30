package com.atri.seduley.ui.screen.schedule

import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.model.Semester
import java.time.LocalDate

/**
 * 课表 UI 状态
 */
sealed class ScheduleUiState {

    /** 加载状态 */
    object Loading : ScheduleUiState()

    /** 成功状态 */
    data class Success(
        val semester: Semester,
        val selectedDate: LocalDate,
        val courses: List<Course>,
        val isOrderSectionVisible: Boolean = false
    ) : ScheduleUiState()

    /** 错误状态 */
    class Error(val message: String) : ScheduleUiState()
}