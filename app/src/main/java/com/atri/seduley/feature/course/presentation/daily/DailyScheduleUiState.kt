package com.atri.seduley.feature.course.presentation.daily

import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import org.threeten.bp.LocalDate

sealed class DailyScheduleUiState {
    object Loading : DailyScheduleUiState()
    data class Success(
        val selectedDate: LocalDate,
        val startDate: LocalDate,
        val endDate: LocalDate,
        val courses: List<CourseDetail>,
        val isOrderSectionVisible: Boolean = false
    ) : DailyScheduleUiState()
    data class Error(val message: String) : DailyScheduleUiState()
}

data class DateCache(
    var selectedDate: LocalDate,
    var startDate: LocalDate,
    var endDate: LocalDate
)
