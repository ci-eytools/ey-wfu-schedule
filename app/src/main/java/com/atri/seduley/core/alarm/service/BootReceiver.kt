package com.atri.seduley.core.alarm.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atri.seduley.core.alarm.domain.model.AlarmState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val alarmManagerService = EntryPointAccessors.fromApplication(
            context.applicationContext,
            AlarmSchedulerEntryPoint::class.java
        ).alarmManagerService()
        CoroutineScope(Dispatchers.IO).launch {
            val alarmsFlow = alarmManagerService.getAllAlarms()
            alarmsFlow.collect { alarmList ->
                alarmList.forEach { alarm ->
                    if (alarm.state == AlarmState.AWAIT) {
                        alarmManagerService.restartAlarm(alarm)
                    }
                }
            }
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AlarmSchedulerEntryPoint {
    fun alarmManagerService(): AlarmManagerService
}