package com.atri.seduley.feature.setting.domain.repository

import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import kotlinx.coroutines.flow.Flow

interface SystemConfigurationRepository {

    /**
     * 保存系统设置
     */
    suspend fun saveSystemConfiguration(systemConfiguration: SystemConfiguration)

    /**
     * 获取系统设置
     */
    fun getSystemConfiguration(): Flow<SystemConfiguration>

    /**
     * 清除系统设置
     */
    suspend fun clear()
}