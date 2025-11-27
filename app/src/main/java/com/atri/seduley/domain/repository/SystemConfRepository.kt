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

    /** 保存是否使用默认封面封面 */
    fun saveDefaultSplash(isDefault: Boolean)

    /** 获取主题颜色 */
    fun getSeedColor(): Int

    /** 订阅主题颜色 */
    fun seedColorFlow(): StateFlow<Int>

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity)

    /** 订阅系统设置信息 */
    fun systemConfInfoFlow(): Flow<SystemConfEntity>

    /** 清除系统设置信息 */
    suspend fun clear()
}