package com.atri.seduley.feature.course.presentation.daily

import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import org.threeten.bp.LocalDate

/**
 * 每日课表 UI 状态
 */
sealed class DailyScheduleUiState {

    /** 加载状态 */
    object Loading : DailyScheduleUiState()

    /** 成功状态 */
    data class Success(
        val selectedDate: LocalDate,         // 当前选择日期
        val startDate: LocalDate,           // 学期开始日期
        val endDate: LocalDate,             // 学期结束日期
        val courses: List<CourseDetail>,    // 课程信息（周）
        val isOrderSectionVisible: Boolean = false  // 是否选中课程（留有状态，暂时无效）
    ) : DailyScheduleUiState()

    /** 错误状态 */
    data class Error(val message: String) : DailyScheduleUiState()
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
