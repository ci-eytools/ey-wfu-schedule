package com.atri.seduley.ui.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.data.local.database.entity.TriggerMode
import com.atri.seduley.data.local.datastore.entity.TaskWay
import com.atri.seduley.data.local.datastore.entity.getMsg
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.randomRequestCode
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.usecase.AuthUseCase
import com.atri.seduley.domain.usecase.CourseUseCase
import com.atri.seduley.domain.usecase.StudentUseCase
import com.atri.seduley.domain.usecase.SystemConfUseCase
import com.atri.seduley.domain.usecase.TaskUseCase
import com.atri.seduley.ui.model.StudentInfo
import com.atri.seduley.ui.model.StudentUpdate
import com.atri.seduley.ui.model.toStudentInfo
import com.atri.seduley.ui.screen.setting.SettingEvent
import com.atri.seduley.ui.screen.setting.SettingUiEvent
import com.atri.seduley.ui.screen.setting.SettingUiEvent.ShowMessage
import com.atri.seduley.ui.screen.setting.SettingUiState
import com.atri.seduley.ui.screen.setting.SettingUiState.Idle
import com.atri.seduley.ui.screen.setting.SettingUiState.Loading
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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
    private val studentUseCase: StudentUseCase,
    private val taskUseCase: TaskUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(Idle)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _event = MutableSharedFlow<SettingUiEvent>()
    val event: SharedFlow<SettingUiEvent> = _event

    val systemConf: StateFlow<SystemConf> = systemConfUseCase.observeSystemConfInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SystemConf(
            notificationWay = TaskWay.STOP,
            updateCourseWay = TaskWay.STOP
        )
    )

    val studentInfos: StateFlow<List<StudentInfo>> = studentUseCase.observeStudents()
        .map { students -> students.map { it.toStudentInfo() }.toList() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentStudentId = authUseCase.observeCurrentStudentId()
        .map { it ?: -1L }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = -1L
        )

    val duration = systemConfUseCase.splashDurationFlow()

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

    // 封面版本号
    private val _coverVersion = MutableStateFlow(0)
    val coverVersion: StateFlow<Int> = _coverVersion

    fun onEvent(event: SettingEvent) {
        try {
            when (event) {
                is SettingEvent.SaveCredential -> saveCredential(event)
                is SettingEvent.SwitchCredential -> switchCurrentId(event.studentId)
                is SettingEvent.ClearCredential -> clearCredential(event.studentId)
                is SettingEvent.UpdateCredential -> updateCredential(event.studentUpdate)
                is SettingEvent.ClearCourses -> clearCourses()
                is SettingEvent.UpdateCourses -> updateCourses()
                is SettingEvent.UpdateCover -> updateCover()
                is SettingEvent.ResetCover -> resetCover()
                is SettingEvent.UpdateSplash -> updateSplash(event.duration)
                is SettingEvent.ResetSplash -> resetSplash()
                is SettingEvent.SwitchNotificationDemand -> switchNotificationDemand(event.taskWay)
                is SettingEvent.SwitchUpdateCourseDemand -> switchUpdateCourseDemand(event.taskWay)
            }
        } catch (e: BaseException) {
            viewModelScope.launch {
                emitMsg(e.message)
            }
        }
    }

    /** 保存用户凭证（需预登录，保证账号密码准确且自动拉取对应课表） */
    private fun saveCredential(event: SettingEvent.SaveCredential) {
        viewModelScope.launch {
            if (event.studentId.isEmpty() || event.password.isEmpty()) {
                emitErr("请输入学号或密码")
                return@launch
            }
            // 1.开始登录，立即显示加载框
            _uiState.value = Loading("正在验证 ${event.studentId} 的凭证...")

            val studentLong = try {
                event.studentId.toLong()
            } catch (_: NumberFormatException) {
                _uiState.value = Idle
                emitErr("学号格式错误")
                return@launch
            }
            // 2.调用登录
            authUseCase.login(
                Credential(
                    studentLong,
                    event.password
                )
            ) {
                // 3.登录成功，更新加载框文本，准备拉取课表
                emitMsg("登录凭证有效，准备拉取课表...")
                _uiState.value =
                    Loading(message = "正在拉取 $studentLong 的课表信息，请稍后...")

                // 4.开始拉取课表
                when (val courseInfo =
                    courseUseCase.updateCourseFromRemote(
                        studentLong
                    )) {
                    is Result.Success -> {
                        // 在回调处更新 nickname
                        studentUseCase.updateNickname(studentLong, event.nickname ?: "")
                        emitMsg("拉取 $studentLong 的课表信息成功")
                    }
                    is Result.Error -> emitErr(courseInfo.msg)
                }
            }.let { loginResult ->
                when (loginResult) {
                    is Result.Success -> _uiState.value = Idle
                    is Result.Error -> {
                        _uiState.value = Idle
                        emitErr(loginResult.msg)
                    }
                }
            }
        }
    }

    /** 切换登录凭证 */
    fun switchCurrentId(studentId: String) {
        launchWithDelayedLoading("正在切换凭证为 $studentId", 0) {
            authUseCase.switchStudent(studentId)
            delay(300)
            _uiState.value = Idle
        }
    }

    /** 删除指定凭证 */
    fun clearCredential(studentId: String) {
        launchWithDelayedLoading("正在删除 $studentId 的凭证") {
            authUseCase.logout(studentId.toLong())
        }
    }

    /** 更新凭证 */
    fun updateCredential(studentUpdate: StudentUpdate) {
        viewModelScope.launch {
            if (studentUpdate.nickname != null) {
                when (studentUseCase.updateNickname(
                    studentUpdate.studentId.toLong(),
                    studentUpdate.nickname
                )) {
                    is Result.Success -> emitMsg("已更新昵称")
                    is Result.Error -> emitErr("未知错误")
                }
            }

            if (studentUpdate.password.isEmpty()) return@launch
            _uiState.value = Loading("正在验证 ${studentUpdate.studentId} 的凭证...")

            val studentLong = try {
                studentUpdate.studentId.toLong()
            } catch (_: NumberFormatException) {
                emitErr("学号格式错误")
                return@launch
            }
            // 2.调用登录
            authUseCase.login(
                Credential(
                    studentLong,
                    studentUpdate.password
                )
            ) {
                // 3.登录成功
                emitMsg("登录凭证有效，已更新密码")
            }.let { loginResult ->
                when (loginResult) {
                    is Result.Success -> _uiState.value = Idle
                    is Result.Error -> {
                        _uiState.value = Idle
                        emitErr(loginResult.msg)
                    }
                }
            }
        }
    }

    /** 清除当前用户的课表信息 */
    private fun clearCourses() {
        launchWithDelayedLoading("正在清除 ${currentStudentId.value} 的课表信息") {
            if (currentStudentId.value == -1L) {
                emitErr("当前无任何登录凭证")
                return@launchWithDelayedLoading
            }
            when (val info = courseUseCase.clearCourse(currentStudentId.value)) {
                is Result.Success -> {
                    emitMsg("删除 ${currentStudentId.value} 的课表信息成功")
                }

                is Result.Error -> emitErr(info.msg)
            }
        }
    }

    /** 向服务器拉取当前用户的课表 */
    private fun updateCourses() {
        viewModelScope.launch {
            if (currentStudentId.value == -1L) {
                emitErr("未登录")
                return@launch
            }
            _uiState.value = Loading("正在拉取 ${currentStudentId.value} 的课表信息...")
            authUseCase.login {
                when (val info =
                    courseUseCase.updateCourseFromRemote(currentStudentId.value)) {
                    is Result.Success -> {
                        _uiState.value = Idle
                        emitMsg("拉取 ${currentStudentId.value} 的课表信息成功")
                    }

                    is Result.Error -> emitErr(info.msg)
                }
            }.let { loginResult ->
                when (loginResult) {
                    is Result.Success -> _uiState.value = Idle
                    is Result.Error -> emitErr(loginResult.msg)
                }
            }
        }
    }

    /** 更新封面 */
    private fun updateCover() {
        launchWithDelayedLoading {
            _coverVersion.value++
            systemConfUseCase.updateSeedColorByCover()
        }
    }

    /** 重置封面 */
    private fun resetCover() {
        launchWithDelayedLoading {
            _coverVersion.value++
            val coverFile = File(context.cacheDir, Const.COVER_IMAGE_NAME)
            val coverGifFile = File(context.cacheDir, Const.GIF_COVER_IMAGE_NAME)
            if (!coverFile.exists() || !coverGifFile.exists()) emitErr("当前已为默认封面")
            coverFile.delete()
            coverGifFile.delete()
            emitMsg("重置封面成功")
            systemConfUseCase.updateSeedColorByCover()  // 更新 datastore
        }
    }

    /** 更新开屏页 */
    private fun updateSplash(duration: Int?) {
        viewModelScope.launch {
            if (duration == null) {
                emitMsg("更新开屏页成功")
                return@launch
            }
            systemConfUseCase.saveSplashDuration(duration)
            emitMsg("更新开屏页持续时间成功")
        }
    }

    /** 重置开屏页 */
    private fun resetSplash() {
        launchWithDelayedLoading {
            val splashFile = File(context.cacheDir, Const.SPLASH_IMAGE_NAME)
            if (!splashFile.exists()) emitErr("当前未设置开屏页")
            splashFile.delete()
            emitMsg("删除开屏页成功")
        }
    }

    /** 切换是否每日提醒 */
    private fun switchNotificationDemand(taskWay: TaskWay) {
        launchWithDelayedLoading {
            val systemConf = systemConf.value
            if (systemConf.notificationWay == taskWay) {
                emitMsg("已更新每日课程提醒触发：${taskWay.getMsg()}")
                return@launchWithDelayedLoading
            }
            if (taskWay.value > 0) {
                val now = LocalDateTime.now()
                taskUseCase.scheduleAlarm(
                    task = Task(
                        triggerAt = TimeUtil.localTimeToLocalDateTime(Const.DAILY_COURSE_NOTIFICATION_TIME),
                        triggerMode = taskWay.toTriggerMode(),
                        callback = Callback.NOTIFICATION_COURSE,
                        state = TaskState.AWAIT,
                        params = mapOf("windowMillis" to (5 * 60 * 1000).toString()),  // 5min 窗口
                        createdAt = now
                    ).randomRequestCode()
                )
            } else {
                taskUseCase.clearTaskByCallback(Callback.NOTIFICATION_COURSE)
            }
            when (systemConfUseCase.saveSystemConfInfo(
                systemConf.copy(
                    notificationWay = taskWay
                )
            )) {
                is Result.Success -> emitMsg("已保存每日课程提醒触发模式：${taskWay.getMsg()}")
                is Result.Error -> emitErr("未知错误")
            }

        }
    }

    /** 切换是否每日更新课表 */
    private fun switchUpdateCourseDemand(taskWay: TaskWay) {
        launchWithDelayedLoading {
            val systemConf = systemConf.value
            if (systemConf.updateCourseWay == taskWay) {
                emitMsg("每日更新课表触发已为： ${taskWay.getMsg()} ")
                return@launchWithDelayedLoading
            }
            if (taskWay.value > 0) {
                val now = LocalDateTime.now()
                taskUseCase.scheduleAlarm(
                    task = Task(
                        triggerAt = TimeUtil.localTimeToLocalDateTime(Const.DAILY_UPDATE_COURSE_TIME),
                        triggerMode = taskWay.toTriggerMode(),
                        callback = Callback.UPDATE_COURSE,
                        state = TaskState.AWAIT,
                        params = mapOf("windowMillis" to (60 * 60 * 1000).toString()),  // 1h 窗口
                        createdAt = now
                    ).randomRequestCode()
                )
            } else {
                taskUseCase.clearTaskByCallback(Callback.UPDATE_COURSE)
            }
            when (systemConfUseCase.saveSystemConfInfo(
                systemConf.copy(
                    updateCourseWay = taskWay
                )
            )) {
                is Result.Success -> emitMsg("已保存每日更新课表触发模式：${taskWay.getMsg()} ")
                is Result.Error -> emitErr("未知错误")
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

    private fun TaskWay.toTriggerMode(): TriggerMode =
        when (this) {
            TaskWay.INEXACT_ALARM -> TriggerMode.INEXACT
            TaskWay.EXACT_ALARM -> TriggerMode.EXACT
            TaskWay.STOP -> TriggerMode.STOP
        }
}