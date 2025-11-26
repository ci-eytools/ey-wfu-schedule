package com.atri.seduley.ui.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.input.key.Key.Companion.I
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.util.Const
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.domain.result.AuthResult
import com.atri.seduley.domain.result.CourseResult
import com.atri.seduley.domain.result.SystemConfResult
import com.atri.seduley.domain.usecase.AuthUseCase
import com.atri.seduley.domain.usecase.CourseUseCase
import com.atri.seduley.domain.usecase.SystemConfUseCase
import com.atri.seduley.ui.screen.setting.SettingEvent
import com.atri.seduley.ui.screen.setting.SettingUiEvent
import com.atri.seduley.ui.screen.setting.SettingUiEvent.ShowMessage
import com.atri.seduley.ui.screen.setting.SettingUiState
import com.atri.seduley.ui.screen.setting.SettingUiState.Idle
import com.atri.seduley.ui.screen.setting.SettingUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.S)
class SettingViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val systemConfUseCase: SystemConfUseCase,
    private val courseUseCase: CourseUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(Idle)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _event = MutableSharedFlow<SettingUiEvent>()
    val event: SharedFlow<SettingUiEvent> = _event

    val studentId = MutableStateFlow("未登录")
    private val _systemConf = MutableStateFlow(
        SystemConf(
            seedColor = Const.DEFAULT_SEED_COLOR_INT,
            isNeedNotification = false,
            isNeedUpdateCourse = true,
            lastUpdatedCourseDate = LocalDateTime.now()
        )
    )
    val systemConf: StateFlow<SystemConf> = _systemConf
    val seedColor: StateFlow<Int> = _systemConf.map { it.seedColor }
        .stateIn(viewModelScope, SharingStarted.Lazily, Const.DEFAULT_SEED_COLOR_INT)

    init {
        viewModelScope.launch {
            when (val info = systemConfUseCase.getSystemConfInfo()) {
                is SystemConfResult.Success -> {
                    info.value?.collect { conf ->
                        _systemConf.value = conf
                    } ?: emitErr()
                }

                is SystemConfResult.UnknownError -> emitErr()
            }

            when (val info = authUseCase.getCurrentStudentId()) {
                is AuthResult.Success -> {
                    info.value?.let { studentId.value = it.studentId } ?: emitErr()
                }

                is AuthResult.UnknownError -> emitErr(info.msg)
                else -> emitErr()
            }
        }
    }

    fun onEvent(event: SettingEvent) {
        when (event) {

            // 保存用户凭证（需预登录，保证账号密码准确且自动拉取对应课表）
            is SettingEvent.SaveCredential -> {
                viewModelScope.launch {
                    if (event.studentId.isEmpty() || event.password.isEmpty()) {
                        emitErr("请输入学号或密码")
                        return@launch
                    }

                    // 1.开始登录，立即显示加载框
                    _uiState.value = Loading("正在验证 ${event.studentId} 的凭证...")

                    // 2.调用登录
                    authUseCase.login(Credential(event.studentId, event.password)) {
                        // 3.登录成功，更新加载框文本，准备拉取课表
                        emitMsg("登录凭证有效，准备拉取课表...")
                        _uiState.value =
                            Loading(message = "正在拉取 ${event.studentId} 的课表信息，请稍后...")

                        // 4.开始拉取课表
                        when (val courseInfo =
                            courseUseCase.updateCourseFromRemote(event.studentId, false)) {
                            is CourseResult.Success -> emitMsg("拉取 ${event.studentId} 的课表信息成功")
                            is CourseResult.AuthError -> emitErr(courseInfo.msg)
                            is CourseResult.UnknownError -> emitErr()
                        }
                    }.let { loginResult ->
                        when (loginResult) {
                            is AuthResult.Success -> _uiState.value = Idle
                            is AuthResult.InvalidCredential -> emitMsg(loginResult.msg)
                            is AuthResult.NetworkError -> emitErr("请检查您的网络连接")
                            is AuthResult.UnknownError -> emitErr(loginResult.msg)
                        }
                    }
                }
            }

            // 清除当前用户的课表信息
            is SettingEvent.ClearSchedules -> {
                launchWithDelayedLoading("正在清除 ${studentId.value} 的课表信息") {
                    when (val info = courseUseCase.clearCourse(studentId.value)) {
                        is CourseResult.Success -> emitMsg("删除 ${studentId.value} 的课表信息成功")
                        is CourseResult.AuthError -> emitErr(info.msg)
                        CourseResult.UnknownError -> emitErr()
                    }
                }
            }

            // 向服务器拉取当前用户的课表
            is SettingEvent.EnterSchedules -> {
                launchWithDelayedLoading("正在拉取 ${studentId.value} 的课表信息") {
                    when (val info = courseUseCase.updateCourseFromRemote(studentId.value)) {
                        is CourseResult.Success -> emitMsg("拉取 ${studentId.value} 的课表信息成功")
                        is CourseResult.AuthError -> emitErr(info.msg)
                        CourseResult.UnknownError -> emitErr()
                    }
                }
            }

            // 更新封面
            SettingEvent.UpdateCover -> {
                launchWithDelayedLoading {
                    systemConfUseCase.updateSeedColorByCover()
                }
            }

            // 重置封面
            is SettingEvent.ResetCover -> {
                launchWithDelayedLoading {
                    val coverFile = File(context.cacheDir, Const.COVER_IMAGE_NAME)
                    if (!coverFile.exists()) emitErr("当前已为默认封面")
                    coverFile.delete()
                    systemConfUseCase.updateSeedColorByCover()  // 更新 datastore
                }
            }

            is SettingEvent.UpdateSplash -> {
                viewModelScope.launch {
                    emitMsg("更新封面成功")
                }
            }

            // 重置开屏页
            is SettingEvent.ResetSplash -> {
                launchWithDelayedLoading {
                    val splashFile = File(context.cacheDir, Const.COVER_IMAGE_NAME)
                    if (!splashFile.exists()) emitErr("当前已为默认开屏页")
                    splashFile.delete()
                    emitMsg("重置开屏页成功")
                }
            }

            // 切换是否每日提醒
            is SettingEvent.SwitchNotificationDemand -> {
                launchWithDelayedLoading {
                    val systemConf = systemConf.first()
                    systemConfUseCase.saveSystemConfInfo(
                        systemConf.copy(
                            isNeedUpdateCourse = !systemConf.isNeedNotification
                        )
                    )
                }
            }

            // 切换是否每日更新课表
            is SettingEvent.SwitchUpdateCourseDemand -> {
                launchWithDelayedLoading {
                    val systemConf = systemConf.first()
                    systemConfUseCase.saveSystemConfInfo(
                        systemConf.copy(
                            isNeedUpdateCourse = !systemConf.isNeedNotification
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
        message: String = "加载中, 请勿关闭软件",
        delayMillis: Long = 300,
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

    /** 发送错误消息 */
    private suspend fun emitErr(msg: String? = null) = emitMsg(msg ?: "系统内部错误")

    /** 发送消息 */
    private suspend fun emitMsg(msg: String) = _event.emit(ShowMessage(msg))
}