package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.core.util.TimeUtil.getNowWeekly
import com.atri.seduley.feature.course.domain.entity.model.Course
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCourses @Inject constructor(
    private val baseInfoRepository: BaseInfoRepository,
    private val courseRepository: CourseRepository
) {

    suspend operator fun invoke(weekly: Int? = null): Flow<List<Course>> {
        val startDate = baseInfoRepository.getBaseInfo().startDate
        return courseRepository.getCoursesByWeekly(weekly ?: getNowWeekly(startDate))
    }
}