package com.atri.seduley.feature.course.domain.use_case

import android.util.Log
import com.atri.seduley.core.util.IdUtil
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.core.util.TimeUtil.calculateWeeks
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.entity.model.Course
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import com.atri.seduley.feature.course.domain.repository.InitInfoRepository
import org.threeten.bp.LocalDate
import javax.inject.Inject

class EnterWeekInfo @Inject constructor(
    private val baseInfoRepository: BaseInfoRepository,
    private val initInfoRepository: InitInfoRepository,
    private val courseRepository: CourseRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String,
        date: LocalDate,
    ) {
        val startDate =
            baseInfoRepository.getBaseInfo().startDate
        val weekly = TimeUtil.getWeekly(TimeUtil.fromTimestampToLocalDate(startDate), date)
        val mask = 1 shl (weekly - 1)
        val enterMark = baseInfoRepository.getBaseInfo().enterMark
        if ((mask and enterMark) != 0) {
            Log.d("InitInfoRepositoryImpl", "日期: $date 已有数据, 跳过")
            return
        }
        val parsedCourses = initInfoRepository.enterInfo(
            username = username,
            password = password,
            date = date
        )

        val courseMap =
            courseRepository.getAllCoursesOnce().associateBy { it.name }.toMutableMap()
        val clazzes = mutableListOf<Clazz>()
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

            // clazz 在数据库进行去重
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
        }
        initInfoRepository.insertInfo(courseMap.values.toList(), clazzes)
    }
}