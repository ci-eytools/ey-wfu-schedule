package com.atri.seduley.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    /** 使用当前用户发起登录请求 */
    suspend fun login()

    /** 使用指定用户发起登录请求 */
    suspend fun loginAs(studentId: String, password: String)

    /** 获取当前登录用户 id */
    fun getCurrentStudentId(): Flow<String?>

    /** 登出/删除当前用户 */
    suspend fun logout()

    /** 登出/删除指定用户 */
    suspend fun logoutAs(studentId: String)

    /** 登出/删除所有用户 */
    suspend fun logoutAll()
}