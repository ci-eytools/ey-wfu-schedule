package com.atri.seduley.ui.screen.setting

import com.atri.seduley.data.local.datastore.entity.TaskWay
import com.atri.seduley.ui.model.StudentUpdate

/**
 * 设置页事件
 */
sealed class SettingEvent {

    /** 保存用户凭证 */
    data class SaveCredential(
        val studentId: String,
        val password: String
    ) : SettingEvent()

    /** 切换当前登录凭证 */
    data class SwitchCredential(val studentId: String) : SettingEvent()

    /** 删除指定凭证 */
    data class ClearCredential(val studentId: String) : SettingEvent()

    /** 更新凭证 */
    data class UpdateCredential(val studentUpdate: StudentUpdate) : SettingEvent()

    /** 清除所有课表 */
    data object ClearCourses : SettingEvent()

    /** 拉取所有课表 */
    data object UpdateCourses : SettingEvent()

    /** 更新封面 */
    data object UpdateCover : SettingEvent()

    /** 重置封面 */
    data object ResetCover : SettingEvent()

    /** 更新开屏页 */
    data object UpdateSplash : SettingEvent()

    /** 重置开屏页 */
    data object ResetSplash : SettingEvent()

    /** 切换每日通知需求 */
    data class SwitchNotificationDemand(val taskWay: TaskWay) : SettingEvent()

    /** 切换每日更新课表需求 */
    data class SwitchUpdateCourseDemand(val taskWay: TaskWay) : SettingEvent()
}