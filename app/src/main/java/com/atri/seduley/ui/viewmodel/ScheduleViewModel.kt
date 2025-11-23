package com.atri.seduley.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.core.util.TimeUtil.toMonday
import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.result.CourseResult
import com.atri.seduley.domain.result.StudentResult
import com.atri.seduley.domain.usecase.CourseUseCase
import com.atri.seduley.domain.usecase.StudentUseCase
import com.atri.seduley.ui.screen.schedule.DateCache
import com.atri.seduley.ui.screen.schedule.ScheduleEvent
import com.atri.seduley.ui.screen.schedule.ScheduleUiState
import com.atri.seduley.ui.screen.schedule.components.SwitchWeekWay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val courseUseCase: CourseUseCase,
    private val studentUseCase: StudentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val courseCache = mutableListOf<Course>()

    var dateCache: DateCache by mutableStateOf(
        DateCache(
            selectedDate = LocalDate.now(),
            startDate = LocalDate.of(LocalDate.now().year, 1, 1),
            endDate = LocalDate.of(LocalDate.now().year, 12, 31)
        )
    )

    init {
        viewModelScope.launch {
            // 加载学期信息
            loadSemester()
            // 加载课表信息
            loadCourses()
        }
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SwitchDate -> {
                launchWithDelayedLoading {
                    if (courseCache.isEmpty()) loadCourses()
                    _uiState.value = ScheduleUiState.Success(
                        selectedDate = event.date,
                        courses = courseCache.filter { it.date == event.date }
                    )
                }
            }

            is ScheduleEvent.SwitchWeek -> {
                launchWithDelayedLoading {
                    if (courseCache.isEmpty()) loadCourses()
                    when (event.where) {
                        SwitchWeekWay.PREVIOUS -> {
                            dateCache.selectedDate.plusWeeks(-1).toMonday()
                        }

                        SwitchWeekWay.NOW -> {
                            dateCache.selectedDate = LocalDate.now()
                        }

                        SwitchWeekWay.NEXT -> {
                            dateCache.selectedDate.plusWeeks(1).toMonday()
                        }
                    }
                    _uiState.value = ScheduleUiState.Success(
                        selectedDate = dateCache.selectedDate,
                        courses = courseCache.filterWeekByDate(dateCache.selectedDate),
                    )
                }
            }

            is ScheduleEvent.SelectCourse -> {
                /** 待开发 */
            }
        }
    }

    /** 加载学期信息 */
    private suspend fun loadSemester() {
        when (val s = studentUseCase.getStudentInfo()) {
            is StudentResult.Success -> {
                dateCache.startDate = s.student.semester.startDate
                dateCache.endDate = s.student.semester.endDate
            }

            is StudentResult.AuthError -> {
                _uiState.value = ScheduleUiState.Error(s.msg)
            }

            StudentResult.UnknownError -> {
                _uiState.value = ScheduleUiState.Error("发生未知错误")
            }
        }
    }

    /** 加载课表信息 */
    private suspend fun loadCourses() {
        when (val c = courseUseCase.getCourses()) {
            is CourseResult.Success -> {
                courseCache.clear()
                courseCache.addAll(c.courses)
            }

            is CourseResult.AuthError -> {
                _uiState.value = ScheduleUiState.Error(c.msg)
            }

            CourseResult.UnknownError -> {
                _uiState.value = ScheduleUiState.Error("发生未知错误")
            }
        }
    }

    /** 根据选定日期按周过滤课程 */
    private fun List<Course>.filterWeekByDate(date: LocalDate): List<Course> {
        return this.filter {
            it.weekly == TimeUtil.getWeekly(
                dateCache.startDate,
                date
            )
        }
    }

    /**
     * 启动延迟加载, 若加载时间大于 [delayMillis] 显示加载组件
     *
     * @param delayMillis 延迟加载组件出现时间
     */
    private fun launchWithDelayedLoading(
        delayMillis: Long = 300,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            val loadingJob = launch {
                delay(delayMillis)
                _uiState.value = ScheduleUiState.Loading
            }
            try {
                block()
            } finally {
                loadingJob.cancel()
            }
        }
    }
}