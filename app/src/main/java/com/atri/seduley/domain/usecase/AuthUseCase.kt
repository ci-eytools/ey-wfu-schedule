package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.model.Student
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.result.AuthResult
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
    ) = toReturn {
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
    ) = toReturn {
        if (studentId.isNullOrEmpty()) {
            authRepository.logout()
        } else {
            authRepository.logoutAs(studentId)
        }
    }

    /** 获取当前用户 */
    suspend fun getCurrentStudentId(): AuthResult = try {
        val studentId = authRepository.getCurrentStudentId()
        if (studentId.isNullOrEmpty()) {
            throw CredentialException("当前未登录任何用户")
        }
        AuthResult.Success(Student(studentId = studentId))
    } catch (e: CredentialException) {
        AuthResult.UnknownError(e.message)
    } catch (e: Exception) {
        AppLogger.e(e)
        AuthResult.UnknownError()
    }

    private suspend fun toReturn(block: suspend () -> Unit): AuthResult {
        return try {
            block()
            AuthResult.Success()
        } catch (e: CredentialException) {
            AuthResult.InvalidCredential(e.message)
        } catch (_: NetworkException) {
            AuthResult.NetworkError
        } catch (e: Exception) {
            AppLogger.e(e)
            AuthResult.UnknownError()
        }
    }
}