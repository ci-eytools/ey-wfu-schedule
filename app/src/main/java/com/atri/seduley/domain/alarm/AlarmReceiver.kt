package com.atri.seduley.domain.alarm

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.alarm.util.ACTION
import com.atri.seduley.core.network.util.NetworkUtils.isNetworkAvailable
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.core.util.SystemCoroutineScope
import com.atri.seduley.data.local.database.entity.Callback
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.copyWithNewParams
import com.atri.seduley.domain.model.nextDay
import com.atri.seduley.domain.model.randomRequestCode
import com.atri.seduley.domain.usecase.TaskUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 接收 AlarmManager 触发事件
 */
@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmDispatcher: AlarmDispatcher

    @Inject
    lateinit var taskUseCase: TaskUseCase

    @Inject
    lateinit var systemCoroutineScope: SystemCoroutineScope

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        AppLogger.i("AlarmReceiver onReceive")
        if (intent.action != ACTION) {
            AppLogger.w("Unknown action: ${intent.action}")
            return
        }

        val requestCode = intent.getIntExtra("requestCode", -1)
        if (requestCode == -1) {
            AppLogger.w("未知 requestCode")
            return
        }

        AppLogger.i("Alarm 触发: requestCode -> $requestCode")

        val pendingResult = goAsync()
        // 1. 从数据库读取对应任务
        systemCoroutineScope.scope.launch {
            var task: Task? = null
            try {
                task = taskUseCase.getTask(requestCode)
                if (task == null) {
                    AppLogger.w("未在数据库查询到 $requestCode 的任务，正在关闭任务")
                    return@launch
                }

                // 2. 分发对应回调
                val newTask = dispatchCallback(context, task)

                // 3. 更新任务
                taskUseCase.scheduleAlarm(newTask)
            } catch (e: Exception) {
                val eStr = e.toString()
                if (task != null) {
                    taskUseCase.updateTask(
                        task.copyWithNewParams(
                            state = TaskState.FAILED,
                            newParams = mapOf("error" to eStr)
                        )
                    )
                }
                AppLogger.w(eStr)
            } finally {
                if (task != null) {
                    when (task.state) {
                        TaskState.AWAIT -> taskUseCase.scheduleAlarm(task)
                        TaskState.FAILED -> {
                            taskUseCase.scheduleAlarm(
                                task.copy(
                                    triggerAt = task.triggerAt.plusDays(1),
                                    state = TaskState.AWAIT,
                                    params = task.params.filter {
                                        // 去除不需要的标记
                                        it.key != "error" && it.key != "retryCount"
                                    }
                                ).randomRequestCode()
                            )
                        }
                        else -> { /* 无其他情况处理 */ }
                    }
                }

                if (task != null && task.state == TaskState.AWAIT) {
                    taskUseCase.scheduleAlarm(task)
                }
                AppLogger.i("结束后台任务 requestCode -> $requestCode")
                pendingResult.finish()
            }
        }
    }

    /** 根据 callback 执行业务逻辑 */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun dispatchCallback(context: Context, task: Task): Task {
        return when (task.callback) {
            Callback.NOTIFICATION_COURSE -> {
                alarmDispatcher.dailyCourseNotification()
                task.nextDay()
            }

            Callback.UPDATE_COURSE -> {
                val isNetworkAvailable = context.isNetworkAvailable()
                AppLogger.d("isNetworkAvailable -> $isNetworkAvailable")
                if (isNetworkAvailable) {
                    alarmDispatcher.updateCourses()
                    task.nextDay()
                } else {
                    val retryCount = (task.params["retryCount"]?.toIntOrNull() ?: 0) + 1
                    if (retryCount >= 3) {
                        // 连续失败三次不再重试，转为设置第二天的定时任务
                        task.copyWithNewParams(newParams = mapOf("retryCount" to "")).nextDay()
                    }
                    val now = LocalDateTime.now()
                    task.copyWithNewParams(
                        triggerAt = now.plusMinutes(30L * retryCount),
                        newParams = mapOf("retryCount" to "${retryCount + 1}")
                    )
                }
            }
        }
    }
}
