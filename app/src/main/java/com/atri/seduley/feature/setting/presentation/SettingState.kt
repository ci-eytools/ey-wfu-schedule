package com.atri.seduley.feature.setting.presentation

/**
 * 设置页 UI 状态
 */
sealed class SettingUiState {

    /** 加载中 */
    data class Loading(
        val message: String = "加载中, 请勿关闭软件"
    ) : SettingUiState()

    /** 空闲状态 */
    object Idle : SettingUiState()
}

/**
 * UI 事件
 */
sealed class SettingUiEvent {

    /** 在底部弹出信息 */
    data class ShowMessage(val message: String) : SettingUiEvent()

    /** 路由返回 */
    object NavigateBack : SettingUiEvent()
}

