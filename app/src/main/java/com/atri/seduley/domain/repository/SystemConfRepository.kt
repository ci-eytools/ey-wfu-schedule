package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * 系统设置信息相关
 */
interface SystemConfRepository {

    /** 保存主题颜色 */
    fun saveSeedColor(color: Int)

    /** 主题颜色流 */
    fun seedColorFlow(): StateFlow<Int>

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity)

    /** 观察系统设置信息 */
    fun systemConfInfoFlow(): Flow<SystemConfEntity>

    /** 清除系统设置信息 */
    suspend fun clear()
}