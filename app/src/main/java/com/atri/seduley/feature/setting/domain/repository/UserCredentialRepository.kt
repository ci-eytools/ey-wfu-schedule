package com.atri.seduley.feature.setting.domain.repository

import com.atri.seduley.feature.setting.domain.entity.UserCredential
import kotlinx.coroutines.flow.Flow

interface UserCredentialRepository {

    /**
     * 保存用户凭证
     */
    suspend fun saveCredential(credential: UserCredential)

    /**
     * 获取学号
     */
    fun getStudentId(): Flow<String>

    /**
     * 在 lambda 中使用密码, 使用后 String 的引用消失, 且立即丢弃 ByteArray
     * 由于后端必须使用 String 类型的密码格式, 必须生成 String, 故为了方便使用直接在此处生成
     */
    suspend fun <T> login(block: suspend (String, String) -> T): T

    /**
     * 清除登录凭证
     */
    suspend fun clear()
}