package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.CredentialDatastore
import com.atri.seduley.data.local.datastore.entity.Credential
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户凭证相关
 */
@Singleton
class CredentialRepository @Inject constructor(
    private val credentialDatastore: CredentialDatastore
) {



    /** 保存用户凭证 */
    suspend fun saveCredential(credential: Credential) = credentialDatastore.saveCredential(credential)

    /** 获取所有已保存的用户 id */
    fun getAllStudentId(): Flow<List<String>> = credentialDatastore.getAllStudentId()

    /** 设置当前登录用户 */
    suspend fun setCurrentStudent(studentId: String) = credentialDatastore.setCurrentStudent(studentId)

    /** 获取当前登录用户 */
    fun getCurrentStudentId(): Flow<String?> = credentialDatastore.getCurrentStudent()

    /** 清除指定用户的凭证 */
    suspend fun clear(studentId: String) = credentialDatastore.clear(studentId)

    /** 清除所有用户凭证 */
    suspend fun clearAll() = credentialDatastore.clearAll()
}