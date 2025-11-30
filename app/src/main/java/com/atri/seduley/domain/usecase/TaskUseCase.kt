package com.atri.seduley.domain.usecase

import com.atri.seduley.core.alarm.AlarmBackend
import com.atri.seduley.core.alarm.AlarmConfig
import com.atri.seduley.core.alarm.AlarmScheduler
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TriggerMode
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.repository.TaskRepository
import javax.inject.Inject

class TaskUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val taskRepository: TaskRepository,
) {

    /** 重设定时任务 */
    suspend fun rescheduleAlarms() {
        taskRepository.getAllAwaitingTasks().forEach { task ->
            alarmScheduler.schedule(
                AlarmConfig(
                    requestCode = task.requestCode,
                    triggerAt = task.triggerAt,
                    windowMillis = task.params["windowMillis"]?.toLong(),
                    backend = task.triggerMode.toBackend()
                )
            )
        }
    }

    /** 启动定时任务 */
    suspend fun scheduleAlarm(task: Task) {
        taskRepository.saveTask(task)
        alarmScheduler.schedule(AlarmConfig(
            requestCode = task.requestCode,
            triggerAt = task.triggerAt,
            windowMillis = task.params["windowMillis"]?.toLong(),
            backend = task.triggerMode.toBackend()
        ))
    }

    /** 清除定时任务 */
    suspend fun clearTaskByCallback(callback: Callback) {
        taskRepository.getAllAwaitingTasks().filter { it.callback == callback }.forEach {
            taskRepository.clearTask(it.requestCode)
            alarmScheduler.cancel(it.requestCode)
        }
    }

    private fun TriggerMode.toBackend() =
        when (this) {
            TriggerMode.INEXACT -> AlarmBackend.INEXACT_ALARM
            TriggerMode.EXACT -> AlarmBackend.EXACT_ALARM
        }
}