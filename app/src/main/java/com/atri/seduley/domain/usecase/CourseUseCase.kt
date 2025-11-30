package com.atri.seduley.domain.usecase

import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository
) {

    /**
     * 观察课表
     *
     * 向数据库请求
     */
    fun observeCourses(
        studentId: Long,
        date: LocalDate
    ): Flow<List<Course>> {
        return courseRepository.observeCoursesByStudentIdAndDate(studentId, date)
    }

    /** 更新课表 */
    suspend fun updateCourseFromRemote(
        studentId: Long
    ): Result<Unit> = toReturn {
        val currStudentId = studentId
        val courses = courseRepository.getAllCoursesFromRemote(currStudentId)
        courseRepository.insertCourses(currStudentId, courses)
    }

    /** 清除课表 */
    suspend fun clearCourse(
        studentId: Long
    ): Result<Unit> = toReturn {
        val currStudentId = studentId
        courseRepository.clearCourses(currStudentId)
    }
}