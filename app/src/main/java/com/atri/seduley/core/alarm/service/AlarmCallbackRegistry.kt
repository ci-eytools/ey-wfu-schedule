package com.atri.seduley.core.alarm.service

import com.atri.seduley.core.alarm.domain.model.AlarmType
import com.atri.seduley.core.alarm.util.AppLogger
import javax.inject.Inject

/**
 * 闹钟回调注册表
 * 用于根据闹钟类型获取相应的回调策略
 */
class AlarmCallbackRegistry @Inject constructor(
    dailyClazzNotificationCallback: DailyClazzNotificationCallback,
    updateScheduleCallback: UpdateScheduleCallback
) {

    private val strategies: Map<AlarmType, AlarmCallback> = mapOf(
        AlarmType.DAILY_CLAZZ_NOTIFICATION to dailyClazzNotificationCallback,
        AlarmType.DAILY_UPDATE_SCHEDULE to updateScheduleCallback
    )

    fun getCallback(alarmType: AlarmType): AlarmCallback? {
        AppLogger.d("getCallback: $alarmType")
        return strategies[alarmType]
    }
}
