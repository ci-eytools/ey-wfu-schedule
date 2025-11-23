package com.atri.seduley.domain.usecase

import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.SystemConfRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class SystemConfUseCase @Inject constructor(
    private val systemConfRepository: SystemConfRepository
) {

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConf: SystemConf) =
        systemConfRepository.saveSystemConfInfo(systemConf.toEntity())

    /** 获取系统设置信息 */
    fun getSystemConfInfo(): Flow<SystemConfEntity> = systemConfRepository.getSystemConfInfo()

    /** 清除系统设置信息 */
    suspend fun clear() = systemConfRepository.clear()
}