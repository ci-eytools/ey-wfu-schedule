package com.atri.seduley.feature.setting.domain.use_case

import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import com.atri.seduley.feature.setting.domain.repository.SystemConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

/**
 * 系统设置用例
 */
data class SystemConfigurationUseCase @Inject constructor(
    val saveSystemConfiguration: SaveSystemConfiguration,
    val getSystemConfiguration: GetSystemConfiguration,
    val clearSystemConfiguration: ClearSystemConfiguration
)

/**
 * 保存系统设置信息
 */
class SaveSystemConfiguration @Inject constructor(
    private val systemConfigurationRepository: SystemConfigurationRepository
) {
    suspend operator fun invoke(
        isNeedNotification: Boolean? = null,
        isNeedUpdateCourse: Boolean? = null,
        lastUpdatedCourseDate: LocalDateTime? = null
    ) {
        val systemConfigurationDB =
            systemConfigurationRepository.getSystemConfiguration().first()
        val systemConfiguration = systemConfigurationDB.copy(
            isNeedNotification = isNeedNotification ?: systemConfigurationDB.isNeedNotification,
            isNeedUpdateCourse = isNeedUpdateCourse ?: systemConfigurationDB.isNeedUpdateCourse,
            lastUpdatedCourseDate = lastUpdatedCourseDate ?: systemConfigurationDB.lastUpdatedCourseDate
        )
        systemConfigurationRepository.saveSystemConfiguration(systemConfiguration)
    }
}

/**
 * 获取系统设置信息
 */
class GetSystemConfiguration @Inject constructor(
    private val systemConfigurationRepository: SystemConfigurationRepository
) {
    operator fun invoke(): Flow<SystemConfiguration> =
        systemConfigurationRepository.getSystemConfiguration()
}

/**
 * 清除系统设置信息
 */
class ClearSystemConfiguration @Inject constructor(
    private val systemConfigurationRepository: SystemConfigurationRepository
) {
    suspend operator fun invoke() {
        systemConfigurationRepository.clear()
    }
}