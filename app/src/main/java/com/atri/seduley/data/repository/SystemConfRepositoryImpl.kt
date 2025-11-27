package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.SystemDataStore
import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.data.local.sp.ThemeProvider
import com.atri.seduley.domain.repository.SystemConfRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息相关
 */
@Singleton
class SystemConfRepositoryImpl @Inject constructor(
    private val systemDataStore: SystemDataStore,
    private val themeProvider: ThemeProvider
) : SystemConfRepository {

    /** 保存主题颜色 */
    override suspend fun saveSeedColor(color: Int) = themeProvider.saveSeedColor(color)

    /** 获取主题颜色 */
    override fun getSeedColor() = themeProvider.getSeedColor()

    /** 订阅主题颜色 */
    override fun seedColorFlow() = themeProvider.seedColorFlow

    /** 保存系统设置信息 */
    override suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity) =
        systemDataStore.saveSystemConfInfo(systemConfiguration)

    /** 订阅系统设置信息 */
    override fun systemConfInfoFlow() = systemDataStore.systemConfInfoFlow()

    /** 清除系统设置信息 */
    override suspend fun clear() = systemDataStore.clear()
}