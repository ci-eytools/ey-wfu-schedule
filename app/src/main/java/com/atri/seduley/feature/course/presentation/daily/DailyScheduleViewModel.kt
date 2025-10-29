package com.atri.seduley.feature.course.presentation.daily

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.use_case.DailyUseCase
import com.atri.seduley.feature.course.presentation.daily.components.SwitchWeekWay
import com.atri.seduley.feature.course.presentation.daily.util.sectionToTime
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class DailyScheduleViewModel @Inject constructor(
    private val dailyUseCase: DailyUseCase,
    baseInfoRepository: BaseInfoRepository,
    private val userCredentialRepository: UserCredentialRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<DailyScheduleUiState>(DailyScheduleUiState.Loading)
    val uiState: State<DailyScheduleUiState> = _uiState

    private val baseInfoFlow = baseInfoRepository.getBaseInfoDTO()

    var dateCache: DateCache by mutableStateOf(
        DateCache(
            selectedDate = LocalDate.now(),
            startDate = LocalDate.now(),
            endDate = LocalDate.now()
        )
    )
        private set

    var getDailyScheduleJob: Job? = null

    init {
        baseInfoFlow.onEach { baseInfo ->
            val newStartDate = TimeUtil.fromTimestampToLocalDate(baseInfo.startDate)
            val newEndDate = TimeUtil.fromTimestampToLocalDate(baseInfo.endDate)
            dateCache = dateCache.copy(startDate = newStartDate, endDate = newEndDate)
            val dateToLoad = if (dateCache.selectedDate == LocalDate.now()) {
                getInitSelectedDate()
            } else {
                dateCache.selectedDate
            }
            loadData(dateToLoad)
        }.catch { e ->
            handleException(e)
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: DailyScheduleEvent) {
        when (event) {

            is DailyScheduleEvent.SwitchDate -> {
                loadData(event.date)
            }

            is DailyScheduleEvent.SwitchWeek -> {
                val newDate = when (event.where) {
                    SwitchWeekWay.PREVIOUS, SwitchWeekWay.NEXT -> {
                        val dayOfWeekOffset = dateCache.selectedDate.dayOfWeek.value - 1
                        dateCache.selectedDate
                            .plusDays(event.where.offset)
                            .plusDays(-dayOfWeekOffset.toLong())
                    }

                    SwitchWeekWay.NOW -> LocalDate.now()
                }
                loadData(newDate)
            }

            is DailyScheduleEvent.SelectCourse -> {
                val currentState = _uiState.value
                if (currentState is DailyScheduleUiState.Success) {
                    _uiState.value = currentState.copy(
                        isOrderSectionVisible = !currentState.isOrderSectionVisible
                    )
                }
            }
        }
    }

    /**
     * 加载边界日期
     */
    private fun loadData(dataToLoad: LocalDate) {
        dateCache = dateCache.copy(selectedDate = dataToLoad)
        launchWithDelayedLoading {
            try {
                enterWeekInfo(dataToLoad)
            } catch (_: NetworkException) {
                AppLogger.d("网络请求失败, 从数据库加载")
            } catch (e: Exception) {
                handleException(e)
                return@launchWithDelayedLoading
            }
            getSchedules(dataToLoad)
        }
    }

    /**
     * 获取课表信息
     */
    private fun getSchedules(date: LocalDate = LocalDate.now()) {
        getDailyScheduleJob?.cancel()
        getDailyScheduleJob = dailyUseCase
            .getCourseDetails(
                startDate = dateCache.startDate,
                selectDate = date
            )
            .onEach { courses ->
                _uiState.value = DailyScheduleUiState.Success(
                    selectedDate = date,
                    courses = courses,
                    isOrderSectionVisible = (_uiState.value as? DailyScheduleUiState.Success)
                        ?.isOrderSectionVisible ?: false,
                    startDate = dateCache.startDate,
                    endDate = dateCache.endDate
                )
            }.catch { e ->
                handleException(e)
            }
            .launchIn(viewModelScope)
    }

    /**
     * 向服务器拉取周信息
     */
    private suspend fun enterWeekInfo(date: LocalDate = LocalDate.now()) {
        userCredentialRepository.login { studentId, password ->
            dailyUseCase.enterWeekInfo(
                username = studentId,
                password = password,
                date = date
            )
        }
    }

    /**
     * 计算开屏后展示的课表日期
     */
    private suspend fun getInitSelectedDate(): LocalDate {
        val today = LocalDate.now()
        val tomorrow = today.plusDays(1)

        val weekCourses = dailyUseCase.getCourseDetails(
            startDate = dateCache.startDate,
            selectDate = today
        ).firstOrNull().orEmpty()

        val todaySections = weekCourses.filter { it.dayOfWeek == today.dayOfWeek.value }
        val hasTodayCourses = todaySections.isNotEmpty()
        val latestEndTime = todaySections
            .maxByOrNull { it.section }
            ?.let { sectionToTime(it.section).end }

        val result = when {
            // 今天没有课程, 且在下午 6 点之后 -> 返回明天
            !hasTodayCourses && LocalTime.now().isAfter(Const.SWITCH_SELECTED_DATE_TOMORROW) -> tomorrow

            // 今天有课程, 且所有课程都已结束 -> 立即返回明天
            hasTodayCourses && latestEndTime?.isBefore(LocalTime.now()) == true -> tomorrow

            // 其他情况 -> 返回今天
            else -> today
        }

        AppLogger.d("今天是否有课: $hasTodayCourses, 最晚结束时间: $latestEndTime, 最终选择日期: $result")
        return result
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
                _uiState.value = DailyScheduleUiState.Loading
            }
            try {
                block()
            } finally {
                loadingJob.cancel()
            }
        }
    }

    /**
     * 集中异常处理
     */
    private fun handleException(e: Throwable) {
        _uiState.value = when (e) {
            is LoginException -> {
                DailyScheduleUiState.Error(e.message)
            }

            is NetworkException -> {
                DailyScheduleUiState.Error(e.message)
            }

            is CredentialException -> {
                DailyScheduleUiState.Error(e.message)
            }

            else -> {
                DailyScheduleUiState.Error("发生未知错误")
            }
        }
    }
}