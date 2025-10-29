package com.atri.seduley.core.alarm.service

import android.R.id.message
import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.TriggerMode
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.util.TimeUtil
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 通用 JobService
 * 在一些主流厂商的 Android 触发不稳定,
 * vivo OriginOS 5 仅应用在前台可以触发, 故不建议使用
 */
@SuppressLint("SpecifyJobSchedulerIdRange")
class JobManagerService : JobService() {

    override fun onStartJob(params: JobParameters?): Boolean {
        if (params == null) return false

        val extras = params.extras
        val jobType = extras.getInt("jobType", -1)

        AppLogger.d("Job 启动, 类型：$jobType")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (JobType.fromInt(jobType)) {

                    JobType.SET_EXACT_MESSAGE_ALARM -> {
                        AppLogger.d("执行消息闹钟任务：$message")

                        val title = extras.getString("title", "")
                        val message = extras.getString("message", "")
                        val time = TimeUtil.fromTimestampToLocalDateTime(
                            extras.getLong("time", -1)
                        )
                        val triggerMode = TriggerMode.fromInt(extras.getInt("triggerMode", -1))

                        val appContext = applicationContext
                        val alarmManagerService = EntryPointAccessors.fromApplication(
                            appContext,
                            AlarmSchedulerEntryPoint::class.java
                        ).alarmManagerService()

                        val alarm = MessageAlarm(
                            title = title,
                            message = message,
                            time = time,
                            triggerMode = triggerMode
                        )

                        alarmManagerService.addAlarm(alarm)
                        AppLogger.d("闹钟添加成功: ${alarm.time}")
                    }

                    else -> {
                        AppLogger.w("未知任务类型: $jobType")
                    }
                }
            } catch (e: Exception) {
                AppLogger.e("Job 执行异常", e)
            } finally {
                jobFinished(params, false)
            }
        }

        // 在后台线程中继续执行
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        AppLogger.d("Job 被系统中止")
        // 尝试重启此任务
        return true
    }
}

enum class JobType(val value: Int) {
    NO_JOB(-1),
    SET_EXACT_MESSAGE_ALARM(0);

    companion object {
        fun fromInt(value: Int) = JobType.entries.firstOrNull { it.value == value } ?: NO_JOB
    }
}
