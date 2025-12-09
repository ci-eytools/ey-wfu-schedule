package com.atri.seduley.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil.toMonday
import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.usecase.AuthUseCase
import com.atri.seduley.domain.usecase.CourseUseCase
import com.atri.seduley.domain.usecase.StudentUseCase
import com.atri.seduley.ui.screen.schedule.ScheduleEvent
import com.atri.seduley.ui.screen.schedule.ScheduleUiState
import com.atri.seduley.ui.screen.schedule.components.SwitchWeekWay
import com.atri.seduley.ui.util.sectionToTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val courseUseCase: CourseUseCase,
    private val studentUseCase: StudentUseCase,
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScheduleUiState>(ScheduleUiState.Loading)
    val uiState: StateFlow<ScheduleUiState> = _uiState

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    val currentStudentId = authUseCase.observeCurrentStudentId()
        .map { it ?: -1L }
        .stateIn(viewModelScope, SharingStarted.Eagerly, -1L)

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val semester = currentStudentId
        .flatMapLatest { id ->
            if (id == -1L) flowOf(null)
            else studentUseCase.observeSemester(id)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val updateTime = currentStudentId
        .filterNotNull()
        .flatMapLatest {
            studentUseCase.observeUpdateTime(it)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LocalDateTime.now()
        )

    init {
        observeData()
        viewModelScope.launch {
            combine(currentStudentId, updateTime) { studentId, lastUpdate ->
                studentId to lastUpdate
            }.collect { (studentId, lastUpdate) ->
                if (studentId != -1L) {
                    checkUpdate(studentId, lastUpdate)
                }
            }
        }
    }

    /** 统一监听数据 */
    private fun observeData() {
        viewModelScope.launch {
            combine(
                semesterFlow(),
                selectedDate,
                coursesFlow()
            ) { sem, date, courses ->

                if (sem.totalWeeks == -1) {
                    return@combine ScheduleUiState.Error("请先登录或拉取数据")
                }

                ScheduleUiState.Success(
                    semester = sem,
                    selectedDate = date,
                    courses = courses
                )
            }
                .catch { e ->
                    emit(ScheduleUiState.Error(e.message ?: "未知错误"))
                }
                .collect {
                    _uiState.value = it
                }
        }

        // 初始化日期
        viewModelScope.launch {
            val date = getInitSelectedDate()
            _selectedDate.emit(date)
        }
    }

    /** 学期 */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun semesterFlow(): Flow<Semester> =
        currentStudentId.flatMapLatest { sid ->
            if (sid == -1L) flowOf(defaultSemester())
            else studentUseCase.observeSemester(sid)
                .map { it ?: defaultSemester() }
        }

    /** 每日课程 */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun coursesFlow(): Flow<List<Course>> =
        combine(currentStudentId, selectedDate) { sid, date ->
            sid to date
        }.flatMapLatest { (sid, date) ->
            if (sid == -1L) flowOf(emptyList())
            else courseUseCase.observeCourses(sid, date)
        }

    fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.SwitchDate -> _selectedDate.value = event.date
            is ScheduleEvent.SwitchWeek -> switchWeek(event)
            is ScheduleEvent.SelectCourse -> {}
        }
    }

    private fun switchWeek(event: ScheduleEvent.SwitchWeek) {
        val monday = selectedDate.value.toMonday()
        val newDate = when (event.where) {
            SwitchWeekWay.PREVIOUS -> monday.plusWeeks(-1)
            SwitchWeekWay.NEXT -> monday.plusWeeks(1)
            SwitchWeekWay.NOW -> LocalDate.now()
        }
        _selectedDate.value = newDate
    }

    /** 根据课程自动判断初始显示日期 */
    private suspend fun getInitSelectedDate(): LocalDate {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)
        val courses = courseUseCase.observeCourses(
            studentId = currentStudentId.filter { it != -1L }.first(),  // 延迟直到收到真实值
            date = today
        ).first()
        val todayCourse = courses.filter { it.date == today }
        val now = LocalTime.now()
        val latestEndTime = todayCourse
            .maxOfOrNull { sectionToTime(it.section).end }

        return when {
            todayCourse.isEmpty() && now.isAfter(Const.SWITCH_SELECTED_DATE_TOMORROW) -> tomorrow
            todayCourse.isNotEmpty() && latestEndTime?.isBefore(now) == true -> tomorrow
            else -> today
        }
    }

    /** 检查课表更新时间，若超过 1 天未更新，自动拉取课表 */
    private fun checkUpdate(studentId: Long, lastUpdate: LocalDateTime?) {
        if (lastUpdate != null && lastUpdate.isBefore(LocalDateTime.now().minusDays(1))) {
            viewModelScope.launch {
                _toastEvent.emit("超过 1 天未更新，自动更新中...")
                authUseCase.login {
                    courseUseCase.updateCourseFromRemote(studentId)
                }.let { info ->
                    when (info) {
                        is Result.Success -> _toastEvent.emit("更新课表成功")
                        is Result.Error -> _toastEvent.emit("更新课表失败：${info.msg}")
                    }
                }
            }
        }
    }

    private fun defaultSemester() = Semester(
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        totalWeeks = -1
    )
}