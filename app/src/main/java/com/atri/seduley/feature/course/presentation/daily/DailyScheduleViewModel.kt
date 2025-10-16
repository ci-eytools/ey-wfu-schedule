package com.atri.seduley.feature.course.presentation.daily

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import javax.inject.Inject

@HiltViewModel
class DailyScheduleViewModel @Inject constructor(
    private val dailyUseCase: DailyUseCase,
    private val baseInfoRepository: BaseInfoRepository,
    private val userCredentialRepository: UserCredentialRepository,
) : ViewModel() {

    private val _uiState = mutableStateOf<DailyScheduleUiState>(DailyScheduleUiState.Loading)
    val uiState: State<DailyScheduleUiState> = _uiState

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
        viewModelScope.launch {
            dateCache = DateCache(
                selectedDate = LocalDate.now(),
                startDate = TimeUtil.fromTimestampToLocalDate(baseInfoRepository.getBaseInfo().startDate),
                endDate = TimeUtil.fromTimestampToLocalDate(baseInfoRepository.getBaseInfo().endDate)
            )
            loadInitData(getInitSelectedDate())
        }
    }

    fun onEvent(event: DailyScheduleEvent) {
        when (event) {

            is DailyScheduleEvent.SwitchDate -> {
                loadInitData(event.date)
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
                dateCache.selectedDate = newDate
                loadInitData(dateCache.selectedDate)
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

    private fun loadInitData(dataToLoad: LocalDate = LocalDate.now()) {
        dateCache.selectedDate = dataToLoad
        launchWithDelayedLoading {
            try {
                try {
                    enterWeekInfo(dateCache.selectedDate)
                } catch (_: NetworkException) {
                    getSchedules(dateCache.selectedDate)
                }
                getSchedules(dateCache.selectedDate)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun getSchedules(date: LocalDate = LocalDate.now()) {
        getDailyScheduleJob?.cancel()
        getDailyScheduleJob = dailyUseCase
            .getCourseDetails(
                startDate = dateCache.startDate,
                selectDate = date
            )
            .onEach { courses ->
                val startDate =
                    TimeUtil.fromTimestampToLocalDate(baseInfoRepository.getBaseInfo().startDate)
                val endDate =
                    TimeUtil.fromTimestampToLocalDate(baseInfoRepository.getBaseInfo().endDate)
                _uiState.value = DailyScheduleUiState.Success(
                    selectedDate = date,
                    courses = courses,
                    isOrderSectionVisible = (_uiState.value as? DailyScheduleUiState.Success)
                        ?.isOrderSectionVisible ?: false,
                    startDate = startDate,
                    endDate = endDate
                )
                dateCache.startDate = startDate
                dateCache.endDate = endDate
            }.catch { e ->
                handleException(e)
            }
            .launchIn(viewModelScope)
    }

    private suspend fun enterWeekInfo(date: LocalDate = LocalDate.now()) {
        userCredentialRepository.login { studentId, password ->
            dailyUseCase.enterWeekInfo(
                username = studentId,
                password = password,
                date = date
            )
        }
    }

    private suspend fun getInitSelectedDate(): LocalDate {
        val endTime = dailyUseCase
            .getCourseDetails(
                startDate = dateCache.startDate,
                selectDate = LocalDate.now()
            ).first()
            .filter { it.dayOfWeek == LocalDate.now().dayOfWeek.value }
            .map { sectionToTime(it.section).end }
            .firstOrNull()
        return if (endTime != null && endTime.isBefore(LocalTime.now())) {
            LocalDate.now().plusDays(1)
        } else {
            LocalDate.now()
        }
    }

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