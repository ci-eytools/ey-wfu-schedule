package com.atri.seduley.core.alarm.service

import android.Manifest
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.alarm.domain.model.Alarm
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.notification.notifier.SystemBarNotification
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import com.atri.seduley.feature.course.presentation.daily.util.sectionToTime
import com.atri.seduley.feature.setting.domain.use_case.CourseUseCase
import com.atri.seduley.feature.setting.domain.use_case.SystemConfigurationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

/**
 * 闹钟回调接口
 */
interface AlarmCallback {
    fun onAlarmTriggered(alarm: Alarm)
}

/**
 * 系统提醒回调
 */
class SystemNotificationCallback @Inject constructor(
    private val systemBarNotification: SystemBarNotification
) : AlarmCallback {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onAlarmTriggered(alarm: Alarm) {
        AppLogger.d("SystemNotificationCallback onAlarmTriggered: $alarm")
        val msg = when (alarm) {
            is MessageAlarm -> alarm.message
            else -> ""
        }
        systemBarNotification.show(alarm.title, msg)
    }
}

/**
 * 每日课程提醒闹钟回调
 */
class DailyClazzNotificationCallback @Inject constructor(
    private val systemBarNotification: SystemBarNotification,
    private val clazzRepository: ClazzRepository
) : AlarmCallback {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onAlarmTriggered(alarm: Alarm) {
        CoroutineScope(Dispatchers.IO).launch {
            val section = clazzRepository.getClazzByDateOnce(LocalDate.now().plusDays(1))
                .minByOrNull { clazz -> clazz.section }?.section
            val msg = when (section) {
                1, 2 -> {
                    val time = sectionToTime(section).start
                    "明日 $time 有课, 早点休息吧 (不要忘记设闹钟哦) "
                }

                3, 4, 5 -> {
                    " 明日上午无课 "
                }

                else -> {
                    "明日无课, 记得取消闹钟哦"
                }
            }
            alarm as MessageAlarm
            val dayOfWeek = alarm.time.dayOfWeek.value
            if (dayOfWeek < 5 || dayOfWeek == 7) {
                systemBarNotification.show(
                    alarm.title,
                    alarm.message.takeIf { !it.isEmpty() } ?: msg)
            }
        }

    }
}

/**
 * 每日更新课程闹钟回调
 */
class UpdateScheduleCallback @Inject constructor(
    private val courseUseCase: CourseUseCase,
    private val systemConfigurationUseCase: SystemConfigurationUseCase
) : AlarmCallback {
    override fun onAlarmTriggered(alarm: Alarm) {
        CoroutineScope(Dispatchers.IO).launch {
            courseUseCase.enterSchedules()
            systemConfigurationUseCase.saveSystemConfiguration(
                lastUpdatedCourseDate = LocalDateTime.now()
            )
        }
    }
}