package com.atri.seduley.feature.setting.domain.repository

import com.atri.seduley.feature.setting.domain.entity.UserCredential

interface UserCredentialRepository {

    suspend fun init()

    suspend fun saveCredential(credential: UserCredential)

    suspend fun getStudentId(): String

    suspend fun <T> login(block: suspend (String, String) -> T): T

    suspend fun clearCredential()
}