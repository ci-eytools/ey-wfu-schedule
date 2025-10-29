package com.atri.seduley.feature.course.data.repository

import com.atri.seduley.feature.course.data.data_score.CourseDao
import com.atri.seduley.feature.course.domain.entity.model.Course
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow

/**
 * 科目存储库实现
 */
class CourseRepositoryImpl(
    private val dao: CourseDao
) : CourseRepository {

    /**
     * 根据 ID 获取 course
     */
    override suspend fun getCourseById(id: Long): Course = dao.getCourseById(id)

    /**
     * 获取所有 course
     */
    override fun getAllCourses(): Flow<List<Course>> = dao.getAllCourses()

    /**
     * 获取所有 course
     */
    override suspend fun getAllCoursesOnce(): List<Course> = dao.getAllCoursesOnce()

    /**
     * 查询指定周的所有 course
     *
     * @param week 周次, 由 `0b10` 开始计数
     */
    override fun getCoursesByWeekly(week: Int): Flow<List<Course>> =
        dao.getCoursesByWeekly((week).coerceAtLeast(0))

    /**
     * 插入 courses
     */
    override suspend fun insertCourses(courses: List<Course>) = dao.insertCourses(courses)

    /**
     * 删除所有 course
     */
    override suspend fun deleteAllCourses() = dao.deleteAllCourses()


}