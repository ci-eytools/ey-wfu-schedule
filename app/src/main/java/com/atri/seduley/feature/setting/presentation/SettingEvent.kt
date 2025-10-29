package com.atri.seduley.feature.setting.presentation

/**
 * 设置页事件
 */
sealed class SettingEvent {

    /** 保存用户凭证 */
    data class SaveCredential(
        val studentId: String? = null,
        val password: String? = null
    ) : SettingEvent()

    /** 清除所有课表 */
    object ClearSchedules : SettingEvent()

    /** 拉取所有课表 */
    object EnterSchedules: SettingEvent()

    /** 重置封面 */
    object ResetCover: SettingEvent()

    /** 更新开屏页 */
    object UpdateSplash: SettingEvent()

    /** 重置开屏页 */
    object ResetSplash: SettingEvent()

    /** 是否需要每日提醒 */
    data class SwitchNotificationDemand(val isNeedNotification: Boolean) : SettingEvent()

    /** 是否需要每日更新课表 */
    data class SwitchUpdateCourseDemand(val isNeedUpdateCourse: Boolean) : SettingEvent()
}