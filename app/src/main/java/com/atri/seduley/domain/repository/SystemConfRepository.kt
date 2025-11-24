package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import kotlinx.coroutines.flow.Flow

/**
 * 系统设置信息相关
 */
interface SystemConfRepository {

    /** 保存封面 uri */
    suspend fun saveCoverUri(uri: String)

    /** 开启封面 uri flow */
    fun coverUriFlow(): Flow<String?>

    /** 保存主题颜色 */
    suspend fun saveSeedColor(color: Int)

    /** 开启主题颜色 flow */
    fun seedColorFlow(): Flow<Int>

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity)

    /** 获取系统设置信息 */
    suspend fun getSystemConfInfo(): SystemConfEntity

    /** 清除系统设置信息 */
    suspend fun clear()
}