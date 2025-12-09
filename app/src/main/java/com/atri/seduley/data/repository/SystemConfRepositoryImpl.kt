package com.atri.seduley.data.repository

import com.atri.seduley.data.local.datastore.SystemDataStore
import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.data.local.sp.SettingsProvider
import com.atri.seduley.domain.repository.SystemConfRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息相关
 */
@Singleton
class SystemConfRepositoryImpl @Inject constructor(
    private val systemDataStore: SystemDataStore,
    private val settingsProvider: SettingsProvider
) : SystemConfRepository {

    /** 保存主题颜色 */
    override fun saveSeedColor(color: Int) = settingsProvider.saveSeedColor(color)

    /** 主题颜色流 */
    override fun seedColorFlow(): StateFlow<Int> = settingsProvider.seedColorFlow

    /** 保存开屏页持续时间 */
    override fun saveSplashDuration(durationMs: Int) {
        settingsProvider.saveSplashDuration(durationMs)
    }

    /** 获取开屏页持续时间 */
    override fun getSplashDuration(): Int {
        return settingsProvider.getSplashDuration()
    }

    /** 开屏页持续时间流 */
    override fun splashDurationFlow(): StateFlow<Int> = settingsProvider.splashDurationFlow

    /** 保存系统设置信息 */
    override suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity) =
        systemDataStore.saveSystemConfInfo(systemConfiguration)

    /** 观察系统设置信息 */
    override fun systemConfInfoFlow() = systemDataStore.systemConfInfoFlow()

    /** 清除系统设置信息 */
    override suspend fun clear() = systemDataStore.clear()
}