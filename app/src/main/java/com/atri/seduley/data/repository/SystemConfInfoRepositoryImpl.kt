package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.SystemDatastore
import com.atri.seduley.data.local.datastore.entity.SystemConfInfo
import com.atri.seduley.domain.repository.SystemConfInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息相关
 */
@Singleton
class SystemConfInfoRepositoryImpl @Inject constructor(
    private val systemDatastore: SystemDatastore
): SystemConfInfoRepository {

    /** 保存系统设置信息 */
    override suspend fun saveSystemConfInfo(systemConfiguration: SystemConfInfo) = systemDatastore.saveSystemConfInfo(systemConfiguration)

    /** 获取系统设置信息 */
    override fun getSystemConfInfo(): Flow<SystemConfInfo> = systemDatastore.getSystemConfInfo()

    /** 清除系统设置信息 */
    override suspend fun clear() = systemDatastore.clear()
}