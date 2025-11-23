package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.result.CourseResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository
) {

    /** 获取课表 */
    suspend fun getCourses(
        studentId: String? = null
    ): List<Course> {
        val currStudentId = parseStudentId(studentId)
        val coursesDB = courseRepository.getCoursesFromDB(currStudentId)
        if (coursesDB.isNotEmpty()) return coursesDB

        val coursesRemote = courseRepository.getCoursesFromRemote(currStudentId)
        courseRepository.updateCourse(currStudentId, coursesRemote)
        return coursesRemote
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
        } catch (e: CredentialException) {
            return CourseResult.AuthError(e.message)
        } catch (e: NetworkException) {
            return CourseResult.AuthError(e.message)
        } catch (_: Exception) {
            return CourseResult.UnknownError
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
        } catch (e: CredentialException) {
            return CourseResult.AuthError(e.message)
        } catch (_: Exception) {
            return CourseResult.UnknownError
        }
    }

    private suspend fun parseStudentId(studentId: String?): String {
        val allIds = authRepository.getAllStudentId().first()
        return when {
            studentId != null && allIds.contains(studentId) -> studentId
            studentId == null && allIds.isNotEmpty() -> allIds.first()
            else -> throw CredentialException("未登录或未找到用户")
        }
    }
}