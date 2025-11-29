package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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
    suspend fun observeCourses(
        studentId: String? = null,
        date: LocalDate = LocalDate.now()
    ): Result<Flow<List<Course?>>> = toReturn {
        val currStudentId = resolveStudentId(studentId?.toLong())
        courseRepository.observeCoursesByStudentIdAndDate(currStudentId, date)
    }

    /** 更新课表 */
    suspend fun updateCourseFromRemote(
        studentId: String? = null,
        isNeedLogin: Boolean = true
    ): Result<Unit> = toReturn {
        val currStudentId = resolveStudentId(studentId?.toLong())
        if (isNeedLogin) authRepository.loginAs(currStudentId)
        val courses = courseRepository.getAllCoursesFromRemote(currStudentId)
        courseRepository.insertCourses(currStudentId, courses)
    }

    /** 清除课表 */
    suspend fun clearCourse(
        studentId: String? = null
    ): Result<Unit> = toReturn {
        val currStudentId = resolveStudentId(studentId?.toLong())
        courseRepository.clearCourses(currStudentId)
    }

    /** 解析用户 id */
    private suspend fun resolveStudentId(studentId: Long?): Long {
        val allIds = authRepository.observeStudentIds().first()
        return when {
            studentId != null && allIds.contains(studentId) -> studentId
            studentId == null && allIds.isNotEmpty() -> allIds.first()
            else -> throw CredentialException("未登录或未找到用户")
        }
    }
}