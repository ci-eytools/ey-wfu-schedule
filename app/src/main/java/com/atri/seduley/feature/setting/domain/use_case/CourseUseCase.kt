package com.atri.seduley.feature.setting.domain.use_case

import android.util.Log
import androidx.room.Transaction
import com.atri.seduley.core.util.IdUtil
import com.atri.seduley.core.util.TimeUtil.calculateWeeks
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.entity.model.Course
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import com.atri.seduley.feature.course.domain.repository.InitInfoRepository
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    val clearSchedules: ClearSchedules,
    val enterSchedules: EnterSchedules
)

class ClearSchedules @Inject constructor(
    private val clazzRepository: ClazzRepository,
    private val courseRepository: CourseRepository,
    private val baseInfoRepository: BaseInfoRepository
) {
    @Transaction
    suspend operator fun invoke() {
        clazzRepository.deleteAllClazzes()
        courseRepository.deleteAllCourses()
        baseInfoRepository.updateEnterMark(0)
    }
}

class EnterSchedules @Inject constructor(
    private val initInfoRepository: InitInfoRepository,
    private val userCredentialRepository: UserCredentialRepository
) {
    suspend operator fun invoke() {
        val parsedCourses = userCredentialRepository.login { studentId, password ->
            initInfoRepository.enterOverallInfo(
                username = studentId,
                password = password
            )
        }
        val courseMap = mutableMapOf<String, Course>()
        val clazzes = mutableListOf<Clazz>()
        var enterMark = 0
        parsedCourses.forEach { parsedCourse ->
            val course = courseMap[parsedCourse.name]
                ?.let {
                    it.copy(
                        credits = parsedCourse.credit,
                        type = parsedCourse.type,
                        weeks = calculateWeeks(
                            weeks = it.weeks,
                            weekly = parsedCourse.weekly
                        )
                    )
                }
                ?: Course(
                    id = IdUtil.nextId(),
                    name = parsedCourse.name,
                    credits = parsedCourse.credit,
                    type = parsedCourse.type,
                    weeks = calculateWeeks(weekly = parsedCourse.weekly)
                )

            // 新 course 加入 courseMap
            courseMap[parsedCourse.name] = course

            clazzes.add(
                Clazz(
                    id = IdUtil.nextId(),
                    courseId = course.id,
                    weekly = parsedCourse.weekly,
                    dayOfWeek = parsedCourse.dayOfWeek,
                    section = parsedCourse.section,
                    date = parsedCourse.date,
                    location = parsedCourse.location,
                )
            )
            enterMark = enterMark or (1 shl (parsedCourse.weekly - 1))
        }
        Log.d("EnterSchedules", clazzes.toString())
        initInfoRepository.insertOverallInfo(
            courses = courseMap.values.toList(),
            clazzes = clazzes,
            enterMark = enterMark
        )
    }
}
