package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.result.CourseResult
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository
) {

    /** 获取课表 */
    suspend fun getCourses(
        studentId: String? = null
    ): CourseResult {
        return try {
            val currStudentId = parseStudentId(studentId)
            val coursesDB = courseRepository.getCoursesFromDB(currStudentId)
            if (coursesDB.isNotEmpty()) CourseResult.Success(coursesDB)
            val coursesRemote = courseRepository.getCoursesFromRemote(currStudentId)
            courseRepository.updateCourse(currStudentId, coursesRemote)
            CourseResult.Success(coursesRemote)
        } catch (e: Exception) {
            handlerException(e)
        }
    }

    /** 更新课表 */
    suspend fun updateCourse(
        studentId: String? = null
    ): CourseResult {
        return try {
            val currStudentId = parseStudentId(studentId)
            authRepository.loginAs(currStudentId)
            val courses = courseRepository.getCoursesFromRemote(currStudentId)
            courseRepository.updateCourse(currStudentId, courses)
            CourseResult.Success(courses)
        } catch (e: Exception) {
            handlerException(e)
        }
    }

    /** 清除课表 */
    suspend fun clearCourse(
        studentId: String? = null
    ): CourseResult {
        return try {
            val currStudentId = parseStudentId(studentId)
            courseRepository.clearCourses(currStudentId)
            CourseResult.Success(emptyList())
        } catch (e: Exception) {
            handlerException(e)
        }
    }

    /** 解析用户 id */
    private suspend fun parseStudentId(studentId: String?): String {
        val allIds = authRepository.getAllStudentId()
        return when {
            studentId != null && allIds.contains(studentId) -> studentId
            studentId == null && allIds.isNotEmpty() -> allIds.first()
            else -> throw CredentialException("未登录或未找到用户")
        }
    }

    /** 异常处理 */
    private fun handlerException(e: Exception): CourseResult {
        return when(e) {
            is CredentialException -> {
                CourseResult.AuthError(e.message)
            }
            is NetworkException -> {
                CourseResult.AuthError(e.message)
            }
            else -> {
                CourseResult.UnknownError
            }
        }
    }
}