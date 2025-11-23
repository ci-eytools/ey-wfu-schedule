package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.database.entity.Course
import java.time.LocalDate

/**
 * 课表相关
 */
interface CourseRepository {

    /** 更新课表 */
    suspend fun updateCourse(studentId: String, courses: List<Course>)

    /** 从本地获取课表 */
    suspend fun getCoursesFromDB(studentId: String): List<Course>

    /** 从远端获取课表
     *
     * @param date 返回该参数所在周的课表
     * @param courses 支持重复传入自动去重
     */
    suspend fun getCoursesFromRemote(
        date: LocalDate,
        courses: MutableList<Course> = mutableListOf()
    ): MutableList<Course>

    /** 清除课表 */
    suspend fun clearCourses(studentId: String)

    /** 清除所有课表 */
    suspend fun clearAllCourses()
}