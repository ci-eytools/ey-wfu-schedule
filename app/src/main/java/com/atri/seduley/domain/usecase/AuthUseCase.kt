package com.atri.seduley.domain.usecase

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.domain.model.Credential
import com.atri.seduley.domain.repository.AuthRepository
import com.atri.seduley.domain.result.AuthResult
import javax.inject.Inject

/**
 * auth 用例
 */
data class AuthUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {

    /** 登录 */
    suspend fun login(
        credential: Credential? = null
    ): AuthResult {

        return try {
            if (credential == null) {
                authRepository.login()
            } else {
                authRepository.loginAs(
                    studentId = credential.studentId,
                    password = credential.password
                )
            }
            AuthResult.Success
        } catch (e: CredentialException) {
            AuthResult.InvalidCredential(e.message)
        } catch (_: NetworkException) {
            AuthResult.NetworkError
        } catch (_: Exception) {
            AuthResult.UnknownError
        }
    }

    /** 登出 */
    suspend fun logout(
        studentId: String? = null
    ): AuthResult {
        return try {
            if (studentId.isNullOrEmpty()) {
                authRepository.logout()
            } else {
                authRepository.logoutAs(studentId)
            }
            AuthResult.Success
        } catch (_: Exception) {
            AuthResult.UnknownError
        }
    }
}