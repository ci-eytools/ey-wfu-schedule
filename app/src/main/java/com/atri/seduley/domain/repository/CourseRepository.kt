package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Course

/**
 * 课表相关
 */
interface CourseRepository {

    /** 更新课表 */
    suspend fun updateCourse(studentId: String, courses: List<Course>)

    /** 从本地获取本学期所有课表 */
    suspend fun getCoursesFromDB(studentId: String): List<Course>

    /** 从远端获取本学期所有课表 */
    suspend fun getCoursesFromRemote(studentId: String): List<Course>

    /** 清除课表 */
    suspend fun clearCourses(studentId: String)

    /** 清除所有课表 */
    suspend fun clearAllCourses()
}