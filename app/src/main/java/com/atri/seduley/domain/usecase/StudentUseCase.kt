package com.atri.seduley.domain.usecase

import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.repository.StudentRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

data class StudentUseCase @Inject constructor(
    private val studentRepository: StudentRepository,
    private val authRepository: AuthRepository
) {

    /** 观察学期信息 */
    fun observeSemester(studentId: Long): Flow<Semester?> {
        return studentRepository.observeSemester(studentId)
    }

    /** 清除学生 */
    suspend fun clearStudent(studentId: Long): Result<Unit> = toReturn {
        studentRepository.clearStudent(studentId)
    }

    /** 观察更新时间 */
    fun observeUpdateTime(studentId: Long): Flow<LocalDateTime?> {
        return studentRepository.observeUpdateTime(studentId)
    }
}