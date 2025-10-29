package com.atri.seduley.core.alarm.service

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.alarm.domain.model.AlarmState
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.feature.setting.domain.use_case.CourseUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 闹钟广播接收器
 */
class AlarmReceiver : BroadcastReceiver() {

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context?, intent: Intent?) {
        AppLogger.d("AlarmReceiver onReceive")
        val alarmId = intent?.getLongExtra("alarmId", -1) ?: return

        val pendingResult = goAsync()

        // 通过 Hilt EntryPoint 获取 AlarmRepository
        val repository = EntryPointAccessors.fromApplication(
            context!!.applicationContext,
            AlarmReceiverEntryPoint::class.java
        ).alarmRepository()

        val callbackRegistry = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmReceiverEntryPoint::class.java
        ).alarmCallbackRegistry()

        val alarmManagerService = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmReceiverEntryPoint::class.java
        ).alarmManagerService()

        val courseUseCase = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmReceiverEntryPoint::class.java
        ).courseUseCase()

        CoroutineScope(Dispatchers.IO).launch {
            val alarm = repository.getAlarmById(alarmId) ?: return@launch
            AppLogger.d("AlarmReceiver onReceive: $alarm")
            val callback = callbackRegistry.getCallback(alarm.type) ?: return@launch
            callback.onAlarmTriggered(alarm)
            alarmManagerService.updateAlarm(alarm.copy(state = AlarmState.DONE))
            pendingResult.finish()
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmReceiverEntryPoint {
    fun alarmRepository(): AlarmRepository
    fun alarmManagerService(): AlarmManagerService
    fun alarmCallbackRegistry(): AlarmCallbackRegistry
    fun courseUseCase(): CourseUseCase
}