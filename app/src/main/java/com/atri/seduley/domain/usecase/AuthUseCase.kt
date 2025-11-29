package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * auth 用例
 */
data class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /**
     * 登录
     *
     * @param block 当传入的 credential 不为空则运行 block()
     */
    suspend fun login(
        credential: Credential? = null,
        block: suspend () -> Unit
    ): Result<Unit> = toReturn {
        if (credential == null) {
            authRepository.login()
        } else {
            authRepository.loginAs(
                studentId = credential.studentId,
                password = credential.password,
                block
            )
        }
    }

    suspend fun switchStudent(
        studentId: String
    ): Result<Unit> = toReturn {
        if (authRepository.observeStudentIds().first().contains(studentId.toLong())) {
            authRepository.saveCurrentStudent(studentId.toLong())
        } else {
            throw CredentialException("账号不存在")
        }
    }

    /** 登出 */
    suspend fun logout(
        studentId: Long? = null
    ): Result<Unit> = toReturn {
        studentId?.let { authRepository.logoutAs(it) } ?: authRepository.logout()
    }

    /** 订阅当前用户 id */
    fun observeCurrentStudentId(): Flow<Long> =
        authRepository.observeCurrentStudentId().map { it ?: -1L }
}