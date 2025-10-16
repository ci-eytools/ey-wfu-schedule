package com.atri.seduley.core.alarm.service

import android.util.Log
import com.atri.seduley.core.alarm.domain.model.AlarmType
import javax.inject.Inject

class AlarmCallbackRegistry @Inject constructor(
    systemNotificationCallback: SystemNotificationCallback,
) {

    private val strategies: Map<AlarmType, AlarmCallback> = mapOf(
        AlarmType.SCHEDULED to systemNotificationCallback,
        AlarmType.MESSAGE to systemNotificationCallback
    )

    fun getCallback(alarmType: AlarmType): AlarmCallback? {
        Log.d("MyAlarm", "getCallback: $alarmType")
        return strategies[alarmType]
    }
}
