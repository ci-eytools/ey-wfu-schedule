package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.SystemDatastore
import com.atri.seduley.data.local.datastore.entity.SystemConfInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息相关
 */
@Singleton
class SystemConfInfoRepository @Inject constructor(
    private val systemDatastore: SystemDatastore
) {

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfInfo) = systemDatastore.saveSystemConfInfo(systemConfiguration)

    /** 获取系统设置信息 */
    fun getSystemConfInfo(): Flow<SystemConfInfo> = systemDatastore.getSystemConfInfo()

    /** 清除系统设置信息 */
    suspend fun clear() = systemDatastore.clear()
}