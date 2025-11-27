package com.atri.seduley.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.util.Const
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
import com.atri.seduley.ui.util.sectionToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val courseUseCase: CourseUseCase,
    private val studentUseCase: StudentUseCase
) : ViewModel() {

    // UI State 的唯一来源
    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // 全量课程数据的缓存
    private var allCoursesCache: List<Course> = emptyList()

    // 日期状态的管理
    var dateCache: DateCache by mutableStateOf(
        DateCache(
            selectedDate = LocalDate.now(),
            startDate = LocalDate.of(LocalDate.now().year, 1, 1),
            endDate = LocalDate.of(LocalDate.now().year, 12, 31)
        )
    )
        private set

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // 在 init 中，_uiState 的初始值就是 Loading，我们不需要再设置
            try {
                // 阶段1: 加载学期信息
                val semester = when (val result = studentUseCase.getStudentInfo()) {
                    is StudentResult.Success -> {
                        dateCache = dateCache.copy(
                            startDate = result.student.semester.startDate,
                            endDate = result.student.semester.endDate
                        )
                        result.student.semester
                    }
                    is StudentResult.AuthError -> {
                        _uiState.value = ScheduleUiState.Error(result.msg)
                        return@launch
                    }
                    StudentResult.UnknownError -> {
                        _uiState.value = ScheduleUiState.Error("获取学期信息失败")
                        return@launch
                    }
                }

                // 阶段2: 加载课程信息
                when (val result = courseUseCase.getCourses()) {
                    is CourseResult.Success -> {
                        // 填充缓存
                        allCoursesCache = result.courses

                        // 计算初始应显示的日期
                        val initialDate = getInitSelectedDate(allCoursesCache)
                        dateCache = dateCache.copy(selectedDate = initialDate) // 同步 dateCache

                        // 阶段3: 所有数据加载成功，更新UI到Success状态
                        _uiState.value = ScheduleUiState.Success(
                            selectedDate = initialDate,
                            courses = allCoursesCache.filter { it.date == initialDate }
                        )
                    }
                    is CourseResult.AuthError -> {
                        _uiState.value = ScheduleUiState.Error(result.msg)
                    }
                    is CourseResult.UnknownError -> {
                        _uiState.value = ScheduleUiState.Error("获取课程信息失败")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = ScheduleUiState.Error("初始化失败: ${e.message}")
            }
        }
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SwitchDate -> {
                // 更新 dateCache 状态
                dateCache = dateCache.copy(selectedDate = event.date)

                // 只有在当前是 Success 状态时才更新，避免覆盖 Loading/Error
                val currentState = _uiState.value
                if (currentState is ScheduleUiState.Success) {
                    _uiState.value = currentState.copy(
                        selectedDate = event.date,
                        courses = allCoursesCache.filter { it.date == event.date }
                    )
                }
            }

            is ScheduleEvent.SwitchWeek -> {
                val newDate = when (event.where) {
                    SwitchWeekWay.PREVIOUS -> dateCache.selectedDate.plusWeeks(-1).toMonday()
                    SwitchWeekWay.NOW -> LocalDate.now()
                    SwitchWeekWay.NEXT -> dateCache.selectedDate.plusWeeks(1).toMonday()
                }
                // 调用 SwitchDate 事件，复用逻辑
                onEvent(ScheduleEvent.SwitchDate(newDate))
            }

            is ScheduleEvent.SelectCourse -> { /* 待开发 */ }
        }
    }

    /** 根据课程计算开屏后应展示的日期 */
    private fun getInitSelectedDate(allCourses: List<Course>): LocalDate {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val todayCourses = allCourses.filter { it.date == today }
        val hasTodayCourses = todayCourses.isNotEmpty()

        val latestEndTime = todayCourses.maxOfOrNull { it.section }?.let { sectionToTime(it).end }
        val now = LocalTime.now()

        return when {
            !hasTodayCourses && now.isAfter(Const.SWITCH_SELECTED_DATE_TOMORROW) -> tomorrow
            hasTodayCourses && latestEndTime?.isBefore(now) == true -> tomorrow
            else -> today
        }
    }
}
/*
package com.atri.seduley.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.util.CoilUtils.result
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.core.util.Const
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
import com.atri.seduley.ui.util.sectionToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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

    private var updateJob: Job? = null
    private var hasLoadedCourses = false

    init {
        viewModelScope.launch {
            if (!hasLoadedCourses && updateJob?.isActive != true) {
                updateJob = launch {
                    // 加载学期信息
                    loadSemester()
                    // 加载课表信息
                    loadCourses()
                    hasLoadedCourses = true
                }
            }
        }
    }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SwitchDate -> {
                viewModelScope.launch {
                    // 如果课程还没加载过，执行更新逻辑
                    if (!hasLoadedCourses && updateJob?.isActive != true) {
                        updateJob = launch {
                            loadCourses()
                            hasLoadedCourses = true
                        }
                    }

                    // 无论如何都刷新UI
                    _uiState.value = ScheduleUiState.Success(
                        selectedDate = event.date,
                        courses = courseCache.filter { it.date == event.date }
                    )
                }
            }

            is ScheduleEvent.SwitchWeek -> {
                launchWithDelayedLoading {
                    if (courseCache.isEmpty()) loadCourses()
                    dateCache.selectedDate = when (event.where) {
                        SwitchWeekWay.PREVIOUS -> {
                            dateCache.selectedDate.plusWeeks(-1).toMonday()
                        }

                        SwitchWeekWay.NOW -> LocalDate.now()

                        SwitchWeekWay.NEXT -> {
                            dateCache.selectedDate.plusWeeks(1).toMonday()
                        }
                    }
                    _uiState.value = ScheduleUiState.Success(
                        selectedDate = dateCache.selectedDate,
                        courses = courseCache.filter { it.date == dateCache.selectedDate }
                    )
                }
            }

            is ScheduleEvent.SelectCourse -> {
                */
/** 待开发 *//*

            }
        }
    }

    */
/** 加载学期信息 *//*

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

    */
/** 加载课表信息 *//*

    private suspend fun loadCourses() {
        when (val c = courseUseCase.getCourses()) {
            is CourseResult.Success -> {
                courseCache.clear()
                courseCache.addAll(c.courses)
                dateCache.selectedDate = getInitSelectedDateAndCourses()
                _uiState.value = ScheduleUiState.Success(
                    selectedDate = dateCache.selectedDate,
                    courses = courseCache.filter { it.date == dateCache.selectedDate }
                )
            }

            is CourseResult.AuthError -> {
                _uiState.value = ScheduleUiState.Error(c.msg)
            }

            is CourseResult.UnknownError -> {
                _uiState.value = ScheduleUiState.Error("发生未知错误")
            }
        }
    }

    */
/** 根据选定日期按周过滤课程 *//*

    private fun List<Course>.filterWeekByDate(date: LocalDate): List<Course> {
        return this.filter {
            it.weekly == TimeUtil.getWeekly(
                dateCache.startDate,
                date
            )
        }
    }

    */
/** 计算开屏后展示的课表日期 *//*

    private fun getInitSelectedDateAndCourses(): LocalDate {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val todayCourses = courseCache.filter { it.date == today }
        val hasTodayCourses = todayCourses.isNotEmpty()

        // 找今天最晚结束的课程时间
        val latestEndTime = todayCourses
            .maxOfOrNull { it.section }
            ?.let { sectionToTime(it).end }

        val now = LocalTime.now()

        val result = when {
            // 今天没有课程且已经过 18:00 → 明天
            !hasTodayCourses && now.isAfter(Const.SWITCH_SELECTED_DATE_TOMORROW) -> tomorrow

            // 今天有课且最后一节下课时间已过 → 明天
            hasTodayCourses && latestEndTime?.isBefore(now) == true -> tomorrow

            // 其他情况 → 今天
            else -> today
        }

        AppLogger.d("今天是否有课: $hasTodayCourses, 最晚结束时间: $latestEndTime, 最终选择日期: $result")

        return result
    }

    */
/**
     * 启动延迟加载, 若加载时间大于 [delayMillis] 显示加载组件
     *
     * @param delayMillis 延迟加载组件出现时间
     *//*

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
}*/
