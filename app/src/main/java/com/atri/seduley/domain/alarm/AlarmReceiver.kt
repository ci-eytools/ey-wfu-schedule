package com.atri.seduley.domain.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.alarm.util.ACTION
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.nextDay
import com.atri.seduley.domain.repository.TaskRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 接收 AlarmManager 触发事件
 */
@AndroidEntryPoint
class AlarmReceiver @Inject constructor(
    private val alarmDispatcher: AlarmDispatcher,
    private val taskRepository: TaskRepository
) : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION) {
            AppLogger.w("Unknown action: ${intent.action}")
            return
        }

        val requestCode = intent.getIntExtra("requestCode", -1)
        if (requestCode == -1) {
            AppLogger.w("Invalid requestCode")
            return
        }

        AppLogger.i("Alarm triggered: requestCode=$requestCode")

        // 1. 从数据库读取对应任务
        scope.launch {
            val task = taskRepository.getTask(requestCode)
            if (task == null) {
                AppLogger.w("Task not found for requestCode=$requestCode")
                return@launch
            }

            // 2. 分发对应回调
            val newTask = dispatchCallback(task)

            // 3. 更新任务
            taskRepository.updateTask(newTask)
        }
    }

    /** 根据 callback 执行业务逻辑 */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun dispatchCallback(task: Task): Task {
        return when (task.callback) {
            Callback.NOTIFICATION_COURSE -> {
                alarmDispatcher.dailyCourseNotification()
                task.nextDay()
            }

            Callback.UPDATE_COURSE -> {
                alarmDispatcher.updateCourses()
                task.nextDay()
            }
        }
    }
}