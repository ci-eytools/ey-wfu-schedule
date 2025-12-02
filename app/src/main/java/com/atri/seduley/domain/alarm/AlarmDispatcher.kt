package com.atri.seduley.domain.alarm

import android.Manifest
import androidx.annotation.RequiresPermission
import com.atri.seduley.core.network.util.NetworkUtils
import com.atri.seduley.core.notification.notifier.SystemBarNotification
import com.atri.seduley.core.util.SystemCoroutineScope
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.repository.StudentRepository
import com.atri.seduley.ui.util.sectionToTime
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

class AlarmDispatcher @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val systemBarNotification: SystemBarNotification,
    private val systemCoroutineScope: SystemCoroutineScope
) {
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun dailyCourseNotification() {
        systemCoroutineScope.scope.launch {
            val now = LocalDate.now()
            val studentId = authRepository.getCurrentStudentId()
            if (studentId == null) {
                return@launch
            }
            val courses = courseRepository.getCoursesByStudentIdAndDate(
                studentId, now.plusDays(1)
            )

            if (courses.isEmpty()) {
                val tomorrow = LocalDate.now().plusDays(1)
                val isWeekendTomorrow = tomorrow.dayOfWeek.value >= 6
                if (!isWeekendTomorrow) {
                    systemBarNotification.show("明日课程提醒", "明日无课，可以睡个好觉啦！")
                }
                return@launch
            }


            val earliestSection = courses.minByOrNull { it.section }?.section
            val dayOfWeek = courses.first().dayOfWeek

            // 辅助变量：是否周五/周六
            val isWeekend = dayOfWeek == 5 || dayOfWeek == 6

            // 根据最早节次生成消息
            val msg = when (earliestSection) {
                null -> {
                    "明日无课, 记得取消闹钟哦 "
                }

                // 1、2节 —— 早课
                in 1..2 -> {
                    val time = sectionToTime(earliestSection).start
                    "明日 $time 有课, 早点休息吧 (不要忘记设闹钟哦) "
                }

                // 3、4、5节 —— 中午前是否有课
                in 3..5 -> {
                    if (isWeekend) {
                        val time = sectionToTime(earliestSection).start
                        "明日 $time 有课, 不要忘记了哦 "
                    } else {
                        "明日上午无课 "
                    }
                }

                else -> {
                    "明日无课, 记得取消闹钟哦 "
                }
            }

            // 周五/周六的特殊推送逻辑
            val shouldNotify = !isWeekend || msg.contains("设闹钟")
            if (shouldNotify) {
                systemBarNotification.show("明日课程提醒", msg)
            }
        }
    }

    fun updateCourses() {
        systemCoroutineScope.scope.launch {
            studentRepository.getAllStudentId().forEach {
                authRepository.loginAs(it, NetworkUtils.createIsolatedOkHttpClient()) {
                    courseRepository.insertCourses(
                        it,
                        courseRepository.getAllCoursesFromRemote(it)
                    )
                }
            }
        }
    }
}