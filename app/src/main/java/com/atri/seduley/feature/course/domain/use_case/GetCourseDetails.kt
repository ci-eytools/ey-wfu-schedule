package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.domain.entity.dto.CourseDetail
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * 获取详细科目信息
 */
class GetCourseDetails @Inject constructor(
    private val courseRepository: CourseRepository,
    private val clazzRepository: ClazzRepository,
) {

    operator fun invoke(
        startDate: LocalDate,
        selectDate: LocalDate? = null
    ): Flow<List<CourseDetail>> {
        val selectDate = selectDate ?: LocalDate.now()
        val weekly = TimeUtil.getWeekly(
            startDate = startDate,
            targetDate = selectDate
        )
        AppLogger.d("$weekly")
        return combine(
            courseRepository.getCoursesByWeekly(weekly),
            clazzRepository.getClazzByDate(selectDate)
        ) { courses, clazzes ->
            AppLogger.d("selectDate: $selectDate")
            AppLogger.d("courses: $courses clazzes: $clazzes")
            clazzes.mapNotNull { clazz ->
                val course = courses.find { it.id == clazz.courseId }
                course?.let {
                    CourseDetail(
                        courseName = it.name,
                        dayOfWeek = clazz.dayOfWeek,
                        section = clazz.section,
                        location = clazz.location
                    )
                }
            }
        }
    }
}