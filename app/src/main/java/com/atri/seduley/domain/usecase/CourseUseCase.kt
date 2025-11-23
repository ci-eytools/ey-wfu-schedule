package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.CourseRepository
import com.atri.seduley.domain.result.CourseResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository
) {

    /** 更新课表 */
    suspend fun updateCourse(
        studentId: String? = null
    ): CourseResult {
        return try {
            val allIds = authRepository.getAllStudentId().first()
            val currStudentId = when {
                studentId != null && allIds.contains(studentId) -> studentId
                studentId == null && allIds.isNotEmpty() -> allIds.first()
                else -> throw CredentialException("未登录或未找到用户")
            }

            authRepository.loginAs(currStudentId)
            val courses = courseRepository.getAllCoursesFromRemote(currStudentId)
            courseRepository.clearCourses(currStudentId)
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
            val allIds = authRepository.getAllStudentId().first()
            val currStudentId = when {
                studentId != null && allIds.contains(studentId) -> studentId
                studentId == null && allIds.isNotEmpty() -> allIds.first()
                else -> throw CredentialException("未登录或未找到用户")
            }
            courseRepository.clearCourses(currStudentId)
            CourseResult.Success(emptyList())
        } catch (e: CredentialException) {
            return CourseResult.AuthError(e.message)
        } catch (_: Exception) {
            return CourseResult.UnknownError
        }
    }
}