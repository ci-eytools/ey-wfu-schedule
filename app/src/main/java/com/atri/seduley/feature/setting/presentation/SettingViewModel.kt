package com.atri.seduley.feature.setting.presentation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.feature.setting.domain.entity.UserCredential
import com.atri.seduley.feature.setting.domain.use_case.CourseUseCase
import com.atri.seduley.feature.setting.domain.use_case.CredentialUseCases
import com.atri.seduley.feature.setting.presentation.util.Assets
import com.atri.seduley.ui.theme.util.ThemePreference
import com.atri.seduley.ui.theme.util.ThemeRepository
import com.atri.seduley.ui.theme.util.ThemeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val credentialUseCase: CredentialUseCases,
    private val courseUseCase: CourseUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<SettingUiState>(SettingUiState.Idle)
    val uiState: StateFlow<SettingUiState> = _uiState

    private val _event = MutableSharedFlow<SettingUiEvent>()
    val event: SharedFlow<SettingUiEvent> = _event

    var studentId: String by mutableStateOf("")
        private set

    var externalResetTrigger: Int by mutableIntStateOf(0)
        private set

    init {
        viewModelScope.launch {
            getStudentId()
        }
    }

    fun onEvent(event: SettingEvent) {
        when (event) {
            is SettingEvent.SaveCredential -> {
                launchWithDelayedLoading {
                    try {
                        credentialUseCase.saveCredential(
                            UserCredential(
                                studentId = event.studentId,
                                password = event.password
                            )
                        )
                        studentId = event.studentId ?: studentId
                        _uiState.value = SettingUiState.Success(
                            studentId = studentId
                        )
                        _event.emit(SettingUiEvent.ShowMessage("保存成功"))
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            is SettingEvent.ClearSchedules -> {
                launchWithDelayedLoading {
                    try {
                        courseUseCase.clearSchedules()
                        _event.emit(SettingUiEvent.ShowMessage("清空所有课程成功"))
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            is SettingEvent.EnterSchedules -> {
                launchWithDelayedLoading {
                    try {
                        courseUseCase.enterSchedules()
                        _event.emit(SettingUiEvent.ShowMessage("拉取所有课程成功"))
                    } catch (e: Exception) {
                        handleException(e)
                    }
                }
            }

            is SettingEvent.ResetCover -> {
                resetCover()
            }

            SettingEvent.UpdateSplash -> {
                launchWithDelayedLoading {
                    _event.emit(SettingUiEvent.ShowMessage("更新开屏页成功"))
                }
            }

            SettingEvent.ResetSplash -> {
                resetSplash()
            }
        }
    }

    private suspend fun getStudentId() {
        try {
            val studentId = credentialUseCase.getStudentId()
            this.studentId = studentId
            _uiState.value = SettingUiState.Success(studentId)
        } catch (e: CredentialException) {
            handleException(e)
        }
    }

    private fun launchWithDelayedLoading(
        delayMillis: Long = 300,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            var loadingShown = false
            val loadingJob = launch {
                delay(delayMillis)
                _uiState.value = SettingUiState.Loading
                loadingShown = true
            }

            try {
                block()
            } finally {
                loadingJob.cancel()
                if (loadingShown) {
                    _uiState.value = SettingUiState.Idle
                }
            }
        }
    }

    private suspend fun handleException(e: Throwable) {
        val message = when (e) {
            is NetworkException -> e.message
            is CredentialException -> e.message
            else -> "发生未知错误"
        }
        _event.emit(SettingUiEvent.ShowMessage(message))
    }

    private fun resetCover() {
        viewModelScope.launch {
            val coverFile = File(context.cacheDir, Assets.COVER_IMAGE_NAME)
            if (!coverFile.exists()) {
                _event.emit(SettingUiEvent.ShowMessage("当前已为默认封面"))
            } else {
                coverFile.delete()
                externalResetTrigger++
                _event.emit(SettingUiEvent.ShowMessage("重置封面成功"))
            }
            ThemeRepository.updateCoverAndSeedColorInStore(context, null)
            ThemeState.seedColor.value = ThemePreference.DEFAULT_SEED_COLOR_INT
        }
    }

    private fun resetSplash() {
        viewModelScope.launch {
            val splashFile = File(context.cacheDir, Assets.SPLASH_IMAGE_NAME)
            if (!splashFile.exists()) {
                _event.emit(SettingUiEvent.ShowMessage("当前已为默认开屏页"))
            } else {
                splashFile.delete()
                _event.emit(SettingUiEvent.ShowMessage("开屏页重置成功"))
            }
        }
    }
}
