package com.atri.seduley.domain.usecase

import com.atri.seduley.core.alarm.AlarmBackend
import com.atri.seduley.core.alarm.AlarmConfig
import com.atri.seduley.core.alarm.AlarmScheduler
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TriggerMode
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.toClear
import com.atri.seduley.domain.model.toTimeOut
import com.atri.seduley.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

class TaskUseCase @Inject constructor(
    private val alarmScheduler: AlarmScheduler,
    private val taskRepository: TaskRepository,
) {

    /** 重设定时任务 */
    suspend fun rescheduleAlarms() {
        for (task in taskRepository.getAllAwaitingTasks()) {
            if (!checkTask(task)) continue
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
        if (task.triggerMode == TriggerMode.STOP) return
        taskRepository.saveTask(task)
        alarmScheduler.schedule(
            AlarmConfig(
                requestCode = task.requestCode,
                triggerAt = task.triggerAt,
                windowMillis = task.params["windowMillis"]?.toLong(),
                backend = task.triggerMode.toBackend()
            )
        )
    }

    /** 清除定时任务 */
    suspend fun clearTaskByCallback(callback: Callback) {
        taskRepository.getAllAwaitingTasks().filter { it.callback == callback }.forEach {
            taskRepository.updateTask(it.toClear())
            alarmScheduler.cancel(it.requestCode)
        }
    }

    /** 更新闹钟，仅更新数据库 */
    suspend fun updateTask(task: Task) = taskRepository.updateTask(task)

    /** 获取闹钟 */
    suspend fun getTask(requestCode: Int) = taskRepository.getTask(requestCode)

    /** 检查定时任务是否正常触发 */
    suspend fun checkTask(task: Task): Boolean {
        // 如果触发时间为空，检查目标触发时间是否在 今天-2天 前
        if (task.actualTriggerAt == null && task.triggerAt.isBefore(
                LocalDateTime.now().minusDays(2)
            )
        ) {
            taskRepository.updateTask(task.toTimeOut())
            alarmScheduler.cancel(task.requestCode)
            return false
        }
        return true
    }

    /** 获取所有定时任务 */
    suspend fun getAllTasks() = taskRepository.getAllTasks()

    /** 批量删除定时任务 */
    suspend fun clearTasks(requestCode: List<Int>) = taskRepository.clearTasks(requestCode)

    private fun TriggerMode.toBackend() = when (this) {
        TriggerMode.INEXACT -> AlarmBackend.INEXACT_ALARM
        TriggerMode.EXACT -> AlarmBackend.EXACT_ALARM
        TriggerMode.STOP -> throw IllegalStateException("STOP mode should not be passed to toBackend()")
    }
}