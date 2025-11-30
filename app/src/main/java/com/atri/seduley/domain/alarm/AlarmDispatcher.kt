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
        systemCoroutineScope.scope.launch  {
            val now = LocalDate.now()
            val studentId = authRepository.getCurrentStudentId()
            if (studentId == null) {
                return@launch
            }
            val courses = courseRepository.getCoursesByStudentIdAndDate(
                studentId, now.plusDays(1)
            )
            val section = courses.minByOrNull { course -> course.section }?.section
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
            val dayOfWeek = courses[0].dayOfWeek
            if (dayOfWeek < 5 || dayOfWeek == 7) {
                systemBarNotification.show("明日课程提醒", msg)
            }
        }
    }

    fun updateCourses() {
        systemCoroutineScope.scope.launch {
            studentRepository.getAllStudentId().forEach {
                courseRepository.clearCourses(it)
                authRepository.loginAs(it, NetworkUtils.createIsolatedOkHttpClient()) {
                    courseRepository.insertCourses(it,
                        courseRepository.getAllCoursesFromRemote(it))
                }
            }
        }
    }
}