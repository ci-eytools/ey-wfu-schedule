package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Course
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * 课表相关
 */
interface CourseRepository {

    /** 插入所有课表
     *
     * 此方法会先删除传入 studentId 的所有课表，再插入新的课表
     */
    suspend fun insertCourses(studentId: Long, courses: List<Course>)

    /** 观察本地每日课表 */
    fun observeCoursesByStudentIdAndDate(studentId: Long, date: LocalDate): Flow<List<Course>>

    /** 获取本地每日课表 */
    suspend fun getCoursesByStudentIdAndDate(studentId: Long, date: LocalDate): List<Course>

    /**
     * 从远端获取本学期所有课表
     *
     * 若不存在当前学生信息，则创建
     */
    suspend fun getAllCoursesFromRemote(studentId: Long): List<Course>

    /** 清除课表 */
    suspend fun clearCourses(studentId: Long)

    /** 清除所有课表 */
    suspend fun clearAllCourses()
}