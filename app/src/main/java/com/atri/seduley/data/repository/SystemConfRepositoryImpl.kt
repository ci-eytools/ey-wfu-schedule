package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.SystemDatastore
import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.domain.repository.SystemConfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息相关
 */
@Singleton
class SystemConfRepositoryImpl @Inject constructor(
    private val systemDatastore: SystemDatastore
): SystemConfRepository {

    /** 保存系统设置信息 */
    override suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity) =
        systemDatastore.saveSystemConfInfo(systemConfiguration)

    /** 获取系统设置信息 */
    override fun getSystemConfInfo(): Flow<SystemConfEntity> = systemDatastore.getSystemConfInfo()

    /** 清除系统设置信息 */
    override suspend fun clear() = systemDatastore.clear()
}