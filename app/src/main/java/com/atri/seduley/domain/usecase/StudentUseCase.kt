package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.StudentRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

data class StudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {

    suspend fun observeSemester(studentId: String? = null): Flow<Semester?> =
        studentRepository.observeSemester(resolveStudentId(studentId?.toLong()))

    suspend fun clearStudent(studentId: String? = null): Result<Unit> = toReturn {
        studentRepository.clearStudent(resolveStudentId(studentId?.toLong()))
    }

    /** 解析用户 id */
    private suspend fun resolveStudentId(studentId: Long?): Long {
        val allIds = authRepository.observeStudentIds().first()
        val currentId = authRepository.observeCurrentStudentId().first()
        return when {
            studentId != null && allIds.contains(studentId) -> studentId
            studentId == null && currentId != null -> currentId
            else -> throw CredentialException("未登录或未找到用户")
        }
    }
}