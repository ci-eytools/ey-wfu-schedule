package com.atri.seduley.ui.screen.schedule

import com.atri.seduley.domain.model.Course
import java.time.LocalDate

/**
 * 课表 UI 状态
 */
sealed class ScheduleUiState {

    /** 加载状态 */
    object Loading : ScheduleUiState()

    /** 成功状态 */
    data class Success(
        val selectedDate: LocalDate,        // 当前选择日期
        val courses: List<Course>,          // 课程信息（周）
        val isOrderSectionVisible: Boolean = false  // 是否选中课程（留有状态，暂时无效）
    ) : ScheduleUiState()

    /** 错误状态 */
    data class Error(val message: String) : ScheduleUiState()
}

/**
 * 缓存日期信息
 */
data class DateCache(

    /** 当前选择日期 */
    var selectedDate: LocalDate,

    /** 学期开始日期 */
    var startDate: LocalDate,

    /** 学期结束日期 */
    var endDate: LocalDate
)
