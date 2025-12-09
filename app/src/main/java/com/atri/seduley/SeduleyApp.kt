package com.atri.seduley

import android.app.Application
import com.atri.seduley.core.exception.GlobalExceptionHandler
import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import com.atri.seduley.core.util.Const
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.domain.usecase.TaskUseCase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@HiltAndroidApp
class SeduleyApp : Application() {

    @Inject
    lateinit var requestHelper: RequestHelper

    @Inject
    lateinit var taskUseCase: TaskUseCase

    override fun onCreate() {
        super.onCreate()
        // 获取系统默认的异常处理器
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                this,
                defaultHandler ?: Thread.UncaughtExceptionHandler { _, _ -> })
        )

        // 网络预热
        CoroutineScope(Dispatchers.IO).launch {
            try {
                requestHelper.get(ApiUrls.LOGIN.toUrl())
            } catch (_: Exception) { /* 预热失败无需处理 */
            }
        }

        // 确定后台任务数据库数量，超过一定值删除
        CoroutineScope(Dispatchers.IO).launch {
            val deleteList = mutableListOf<Int>()
            val allAlarms = taskUseCase.getAllTasks().filter { it.state != TaskState.AWAIT }
            if (allAlarms.size > Const.DELECT_ALARM_NUM) {
                // 优先删除七天前的数据
                deleteList.addAll(
                    allAlarms
                        .filter { it.createdAt.isBefore(LocalDateTime.now().minusDays(7)) }
                        .map { it.requestCode })
                if (deleteList.size < Const.DELECT_ALARM_NUM) {
                    deleteList.addAll(
                        allAlarms.sortedByDescending { it.createdAt }
                            .drop(Const.DELECT_ALARM_NUM - deleteList.size)
                            .map { it.requestCode }
                    )
                }
                taskUseCase.clearTasks(deleteList)
            }
        }

        // 固定时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
    }
}