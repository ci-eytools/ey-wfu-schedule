package com.atri.seduley.feature.setting.presentation

sealed class SettingEvent {
    data class SaveCredential(
        val studentId: String? = null,
        val password: String? = null
    ) : SettingEvent()

    object ClearSchedules : SettingEvent()
    object EnterSchedules: SettingEvent()
    object ResetCover: SettingEvent()
    object UpdateSplash: SettingEvent()
    object ResetSplash: SettingEvent()
}