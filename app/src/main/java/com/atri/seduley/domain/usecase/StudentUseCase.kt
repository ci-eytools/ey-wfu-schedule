package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.StudentRepository
import com.atri.seduley.domain.result.StudentResult
import javax.inject.Inject

data class StudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {

    suspend fun getStudentInfo(
        studentId: String? = null
    ): StudentResult {
        return try {
            val currStudentId = parseStudentId(studentId)
            val student = studentRepository.getStudentInfo(currStudentId)
            StudentResult.Success(student)
        } catch (e: CredentialException) {
            return StudentResult.AuthError(e.message)
        } catch (e: Exception) {
            AppLogger.e(e)
            return StudentResult.UnknownError
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
}