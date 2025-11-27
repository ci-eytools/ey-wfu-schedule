package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import com.atri.seduley.domain.result.toReturnSync
import kotlinx.coroutines.flow.Flow
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

    /** 登出 */
    suspend fun logout(
        studentId: String? = null
    ): Result<Unit> = toReturn {
        if (studentId.isNullOrEmpty()) {
            authRepository.logout()
        } else {
            authRepository.logoutAs(studentId)
        }
    }

    /** 订阅当前用户 id */
    fun currStudentIdFlow(): Result<Flow<String>> =
        toReturnSync { authRepository.currStudentIdFlow() }

    /** 获取当前用户 */
    fun getCurrentStudentId(): Result<String> = toReturnSync {
        val studentId = authRepository.getCurrStudentId()
        if (studentId.isNullOrEmpty()) {
            throw CredentialException("当前未登录任何用户")
        }
        studentId
    }
}