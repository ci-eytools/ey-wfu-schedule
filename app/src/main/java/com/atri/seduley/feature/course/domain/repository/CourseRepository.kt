package com.atri.seduley.feature.course.domain.repository

import com.atri.seduley.feature.course.domain.entity.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    /**
     * 根据 ID 获取 course
     */
    suspend fun getCourseById(id: Long): Course

    /**
     * 获取所有 course
     */
    fun getAllCourses(): Flow<List<Course>>

    /**
     * 获取所有 course
     */
    suspend fun getAllCoursesOnce(): List<Course>

    /**
     * 查询指定周的所有 course
     *
     * @param week 周次, 由 `0b10` 开始计数
     */
    fun getCoursesByWeekly(week: Int): Flow<List<Course>>

    /**
     * 插入 courses
     */
    suspend fun insertCourses(courses: List<Course>)

    /**
     * 删除所有 course
     */
    suspend fun deleteAllCourses()
}