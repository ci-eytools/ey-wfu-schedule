package com.atri.seduley.feature.course.domain.use_case

import javax.inject.Inject

data class DailyUseCase @Inject constructor(
    val getBaseInfo: GetBaseInfo,
    val getClazz: GetClazz,
    val getCourses: GetCourses,
    val getCourseDetails: GetCourseDetails,
    val enterWeekInfo: EnterWeekInfo
)