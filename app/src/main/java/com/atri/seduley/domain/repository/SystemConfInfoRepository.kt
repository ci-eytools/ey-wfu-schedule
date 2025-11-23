package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.datastore.entity.SystemConfInfoEntity
import kotlinx.coroutines.flow.Flow

/**
 * 系统设置信息相关
 */
interface SystemConfInfoRepository {

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfInfoEntity)

    /** 获取系统设置信息 */
    fun getSystemConfInfo(): Flow<SystemConfInfoEntity>

    /** 清除系统设置信息 */
    suspend fun clear()
}