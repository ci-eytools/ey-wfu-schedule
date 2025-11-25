package com.atri.seduley.ui.screen.setting

/**
 * 设置页事件
 */
sealed class SettingEvent {

    /** 保存用户凭证 */
    data class SaveCredential(
        val studentId: String,
        val password: String
    ) : SettingEvent()

    /** 清除所有课表 */
    data object ClearSchedules : SettingEvent()

    /** 拉取所有课表 */
    data object EnterSchedules : SettingEvent()

    /** 更新封面 */
    data object UpdateCover : SettingEvent()

    /** 重置封面 */
    data object ResetCover : SettingEvent()

    /** 更新开屏页 */
    data object UpdateSplash : SettingEvent()

    /** 重置开屏页 */
    data object ResetSplash : SettingEvent()

    /** 是否需要每日提醒 */
    data class SwitchNotificationDemand(val isNeedNotification: Boolean) : SettingEvent()

    /** 是否需要每日更新课表 */
    data class SwitchUpdateCourseDemand(val isNeedUpdateCourse: Boolean) : SettingEvent()
}