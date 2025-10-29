package com.atri.seduley.core.alarm

import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.service.AlarmManagerService
import javax.inject.Inject

/**
 * 对外暴露的闹钟管理接口
 * 支持创建 / 修改 / 删除 / 暂停 / 查询
 * 支持不同闹钟回调，可自定义通知行为
 */
class AlarmController @Inject constructor(
    private val alarmService: AlarmManagerService
) {

    /**
     * 创建闹钟
     */
    suspend fun createAlarm(alarm: Alarm) = alarmService.addAlarm(alarm)

    /**
     * 删除闹钟
     */
    suspend fun deleteAlarm(alarm: Alarm) = alarmService.deleteAlarm(alarm)

    /**
     * 检查闹钟是否存在数据库（根据 time, type, triggerMode）
     */
    suspend fun checkExists(alarm: Alarm): Boolean =
        alarmService.findAlarmByTimeOrTypeOrTriggerMode(
            alarm.time,
            alarm.type,
            alarm.triggerMode
        ).isNotEmpty()
}