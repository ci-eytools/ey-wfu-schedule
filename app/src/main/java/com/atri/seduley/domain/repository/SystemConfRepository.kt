package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import kotlinx.coroutines.flow.Flow

/**
 * 系统设置信息相关
 */
interface SystemConfRepository {

    /** 保存主题颜色 */
    suspend fun saveSeedColor(color: Int)

    /** 获取主题颜色 */
    fun getSeedColor(): Int

    /** 订阅主题颜色 */
    fun seedColorFlow(): Flow<Int>

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity)

    /** 订阅系统设置信息 */
    fun systemConfInfoFlow(): Flow<SystemConfEntity>

    /** 清除系统设置信息 */
    suspend fun clear()
}