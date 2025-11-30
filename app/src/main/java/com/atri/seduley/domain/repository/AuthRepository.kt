package com.atri.seduley.domain.repository

import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient

interface AuthRepository {

    /** 使用当前用户发起登录请求 */
    suspend fun login(block: suspend () -> Unit)

    /** 使用指定已存在用户发起登录请求 */
    suspend fun loginAs(studentId: Long, block: suspend () -> Unit)

    /** 使用指定已存在用户发起登录请求，使用传入的 OkHttpClient 实例 */
    suspend fun loginAs(studentId: Long, isolatedClient: OkHttpClient, block: suspend () -> Unit)

    /** 使用指定新用户发起登录请求 */
    suspend fun loginAs(studentId: Long, password: String, block: suspend () -> Unit)

    /** 观察当前登录用户 id */
    fun observeCurrentStudentId(): Flow<Long?>

    /** 获取当前登录用户 id */
    suspend fun getCurrentStudentId(): Long?

    /** 观察所有用户 id */
    fun observeStudentIds(): Flow<List<Long>>

    /** 切换当前登录用户 */
    suspend fun saveCurrentStudent(studentId: Long)

    /** 登出/删除当前用户 */
    suspend fun logout()

    /** 登出/删除指定用户 */
    suspend fun logoutAs(studentId: Long)

    /** 登出/删除所有用户 */
    suspend fun logoutAll()
}