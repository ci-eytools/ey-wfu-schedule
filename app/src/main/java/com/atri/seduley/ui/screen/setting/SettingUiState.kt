package com.atri.seduley.ui.screen.setting

/**
 * 设置页 UI 状态
 */
sealed class SettingUiState {

    /** 空闲状态 */
    object Idle : SettingUiState()

    /** 加载中 */
    class Loading(
        val message: String = "加载中, 请勿关闭软件"
    ) : SettingUiState()
}

/**
 * UI 事件
 */
sealed class SettingUiEvent {

    /** 在底部弹出信息 */
    class ShowMessage(val message: String) : SettingUiEvent()

    /** 路由返回 */
    object NavigateBack : SettingUiEvent()
}

