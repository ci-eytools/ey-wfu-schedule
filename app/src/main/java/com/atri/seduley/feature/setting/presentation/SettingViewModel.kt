package com.atri.seduley.feature.setting.presentation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.alarm.AlarmController
import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import com.atri.seduley.core.alarm.domain.model.TriggerMode
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import com.atri.seduley.feature.setting.domain.entity.UserCredential
import com.atri.seduley.feature.setting.domain.use_case.CourseUseCase
import com.atri.seduley.feature.setting.domain.use_case.CredentialUseCases
import com.atri.seduley.feature.setting.domain.use_case.SystemConfigurationUseCase
import com.atri.seduley.feature.setting.presentation.SettingUiEvent.ShowMessage
import com.atri.seduley.feature.setting.presentation.SettingUiState.Idle
import com.atri.seduley.feature.setting.presentation.SettingUiState.Loading
import com.atri.seduley.ui.theme.util.ThemePreference
import com.atri.seduley.ui.theme.util.ThemeRepository
import com.atri.seduley.ui.theme.util.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import java.io.File
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class SettingViewModel @Inject constructor(
    private val credentialUseCases: CredentialUseCases,
    private val courseUseCase: CourseUseCase,
    private val systemConfigurationUseCase: SystemConfigurationUseCase,
    private val alarmController: AlarmController,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(Idle)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _event = MutableSharedFlow<SettingUiEvent>()
    val event: SharedFlow<SettingUiEvent> = _event

    private val _appScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val systemConfigState: StateFlow<SystemConfiguration> =
        systemConfigurationUseCase.getSystemConfiguration()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SystemConfiguration(
                    isNeedNotification = false,
                    isNeedUpdateCourse = false,
                    lastUpdatedCourseDate = Const.NO_LAST_UPDATE_SELECTED_DATE
                )
            )

    val studentIdState: StateFlow<String> = credentialUseCases.getStudentId()
        .catch { emit("") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val externalResetTrigger: StateFlow<Int>
        get() = _externalResetTrigger
    private val _externalResetTrigger = MutableStateFlow(0)


    init {
        combine(
            systemConfigState,
            studentIdState
        ) { config, studentId ->
            Pair(config, studentId)
        }.onEach { (config, studentId) ->
            // 处理每日提醒
            if (config.isNeedNotification && studentId.isNotEmpty()) {
                setupDailyNotification()
            } else if (!config.isNeedNotification) {
                cancelDailyNotification()
            }
            // 处理每日更新课程
            if (config.isNeedUpdateCourse && studentId.isNotEmpty()) {
                setupUpdateCourse()
            } else if (!config.isNeedUpdateCourse) {
                cancelUpdateCourse()
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: SettingEvent) {
        when (event) {

            // 保存用户凭证
            is SettingEvent.SaveCredential -> {
                viewModelScope.launch {
                    try {
                        launchWithDelayedLoadingAsync(message = "正在验证用户凭证...") {
                            val credential = UserCredential(
                                studentId = event.studentId,
                                password = event.password
                            )
                            credentialUseCases.saveCredential(credential)
                        }.await() // 等待凭证保存任务完成

                        _event.emit(ShowMessage("验证用户凭证成功"))

                        launchWithDelayedLoadingAsync(message = "正在拉取课程...") {
                            courseUseCase.enterSchedules()
                            systemConfigurationUseCase.saveSystemConfiguration(
                                lastUpdatedCourseDate = LocalDateTime.now()
                            )
                        }.await() // 等待课程拉取任务完成

                        _event.emit(ShowMessage("拉取课程成功"))

                    } catch (e: Exception) {
                        handleException(e)
                    } finally {
                        if (_uiState.value is Loading) {
                            _uiState.value = Idle
                        }
                    }
                }
            }

            // 清除所有课表信息
            is SettingEvent.ClearSchedules -> {
                launchWithDelayedLoading {
                    try {
                        courseUseCase.clearSchedules()
                        _event.emit(ShowMessage("清空所有课程成功"))
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            // 向服务器拉取所有课表
            is SettingEvent.EnterSchedules -> {
                launchWithDelayedLoading {
                    try {
                        courseUseCase.enterSchedules()
                        _event.emit(ShowMessage("拉取所有课程成功"))
                        systemConfigurationUseCase.saveSystemConfiguration(
                            lastUpdatedCourseDate = LocalDateTime.now()
                        )
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            // 重置封面
            is SettingEvent.ResetCover -> resetCover()

            // 更新开屏页 (仅做消息提醒, 具体逻辑在 component)
            is SettingEvent.UpdateSplash -> {
                launchWithDelayedLoading { _event.emit(ShowMessage("更新开屏页成功")) }
            }

            // 重置开屏页
            is SettingEvent.ResetSplash -> resetSplash()

            // 切换是否每日提醒
            is SettingEvent.SwitchNotificationDemand -> {
                viewModelScope.launch {
                    systemConfigurationUseCase
                        .saveSystemConfiguration(
                            isNeedNotification = event.isNeedNotification
                        )
                    _event.emit(
                        ShowMessage(
                            "已${
                                if (event.isNeedNotification) "开启" else "关闭"
                            } 每日提醒"
                        )
                    )
                }
            }

            // 切换是否每日更新课表
            is SettingEvent.SwitchUpdateCourseDemand -> {
                viewModelScope.launch {
                    systemConfigurationUseCase.saveSystemConfiguration(
                        isNeedUpdateCourse = event.isNeedUpdateCourse
                    )
                    _event.emit(
                        ShowMessage(
                            "已${
                                if (event.isNeedUpdateCourse) "开启" else "关闭"
                            } 每日课表更新"
                        )
                    )
                }
            }
        }
    }

    /**
     * 启动延迟加载, 若加载时间大于 [delayMillis] 显示加载组件
     *
     * @param delayMillis 延迟加载组件出现时间
     */
    private fun launchWithDelayedLoading(
        delayMillis: Long = 300,
        message: String = "加载中, 请勿关闭软件",
        block: suspend () -> Unit
    ): Job {
        return viewModelScope.launch {
            var loadingJob: Job? = null
            try {
                loadingJob = launch {
                    delay(delayMillis)
                    _uiState.value = Loading(message)
                }
                block()
            } catch (e: Exception) {
                throw e
            } finally {
                loadingJob?.cancel()
            }
        }
    }

    /**
     * 启动延迟加载 （同步), 若加载时间大于 [delayMillis] 显示加载组件
     *
     * @param delayMillis 延迟加载组件出现时间
     */
    private fun launchWithDelayedLoadingAsync(
        delayMillis: Long = 300,
        message: String = "加载中, 请勿关闭软件",
        block: suspend () -> Unit
    ): Deferred<Unit> {
        return viewModelScope.async {
            var loadingJob: Job? = null
            try {
                loadingJob = launch {
                    delay(delayMillis)
                    _uiState.value = Loading(message)
                }
                block()
            } finally {
                loadingJob?.cancel()
            }
        }
    }

    /**
     * 异常处理
     */
    private suspend fun handleException(e: Exception) {
        val message = when (e) {
            is NetworkException, is CredentialException -> e.message
            else -> "发生未知错误"
        }
        _event.emit(ShowMessage(message))
    }

    /**
     * 重置封面
     */
    private fun resetCover() {
        viewModelScope.launch {
            val coverFile = File(context.cacheDir, Const.COVER_IMAGE_NAME)
            if (!coverFile.exists()) {
                _event.emit(ShowMessage("当前已为默认封面"))
            } else {
                coverFile.delete()
                _externalResetTrigger.value++
                _event.emit(ShowMessage("重置封面成功"))
            }
            ThemeRepository.updateCoverAndSeedColorInStore(context, null)
            ThemeState.seedColor.value = ThemePreference.DEFAULT_SEED_COLOR_INT
        }
    }

    /**
     * 重置开屏页
     */
    private fun resetSplash() {
        viewModelScope.launch {
            val splashFile = File(context.cacheDir, Const.SPLASH_IMAGE_NAME)
            if (!splashFile.exists()) {
                _event.emit(ShowMessage("当前已为默认开屏页"))
            } else {
                splashFile.delete()
                _event.emit(ShowMessage("开屏页重置成功"))
            }
        }
    }

    /**
     * 设置每日课程提醒
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupDailyNotification() {
        _appScope.launch {
            val notifyTime =
                TimeUtil.localTimeToLocalDateTime(Const.DAILY_CLAZZ_NOTIFICATION_TIME)
            val alarm = MessageAlarm(
                title = "明日课程提醒",
                time = notifyTime,
                message = "",
                type = AlarmType.DAILY_CLAZZ_NOTIFICATION,
                triggerMode = TriggerMode.DAILY_ALARM
            )
            if (!alarmController.checkExists(alarm)) {
                alarmController.createAlarm(alarm)
            }
        }
    }

    /**
     * 取消每日课程提醒
     */
    private fun cancelDailyNotification() {
        _appScope.launch {
            val alarm = MessageAlarm(
                title = "明日课程提醒",
                time = TimeUtil.localTimeToLocalDateTime(Const.DAILY_CLAZZ_NOTIFICATION_TIME),
                message = "",
                type = AlarmType.DAILY_CLAZZ_NOTIFICATION,
                triggerMode = TriggerMode.DAILY_ALARM
            )
            alarmController.deleteAlarm(alarm)
        }
    }

    /**
     * 设置每日更新课表
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupUpdateCourse() {
        _appScope.launch {
            val notifyTime =
                TimeUtil.localTimeToLocalDateTime(Const.DAILY_UPDATE_COURSE_TIME)
            val alarm = ScheduledAlarm(
                title = "更新课程信息",
                time = notifyTime,
                type = AlarmType.DAILY_UPDATE_SCHEDULE,
                triggerMode = TriggerMode.DAILY_ALARM
            )
            if (!alarmController.checkExists(alarm)) {
                alarmController.createAlarm(alarm)
            }
        }
    }

    /**
     * 取消每日更新课表
     */
    private fun cancelUpdateCourse() {
        _appScope.launch {
            val alarm = ScheduledAlarm(
                title = "更新课程信息",
                time = TimeUtil.localTimeToLocalDateTime(Const.DAILY_UPDATE_COURSE_TIME),
                type = AlarmType.DAILY_UPDATE_SCHEDULE,
                triggerMode = TriggerMode.DAILY_ALARM
            )
            alarmController.deleteAlarm(alarm)
        }
    }
}