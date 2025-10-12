package com.atri.seduley.feature.setting.presentation

sealed class SettingUiState {
    object Loading : SettingUiState()
    data class Success(
        val studentId: String = ""
    ) : SettingUiState()
    object Idle : SettingUiState() // 默认空闲态
}

sealed class SettingUiEvent {
    data class ShowMessage(val message: String) : SettingUiEvent()
    object NavigateBack : SettingUiEvent()
}

