package com.atri.seduley.feature.setting.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.setting.domain.entity.SystemConfiguration
import com.atri.seduley.feature.setting.domain.repository.SystemConfigurationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 系统设置存储库实现
 */
class SystemConfigurationRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SystemConfigurationRepository {

    private object Keys {
        val IS_NEED_NOTIFICATION = booleanPreferencesKey("isNeedNotification")
        val IS_NEED_UPDATE_COURSE = booleanPreferencesKey("isNeedUpdateCourse")
        val LAST_UPDATED_COURSE_DATE = longPreferencesKey("lastUpdatedCourseDate")
    }

    /**
     * 保存系统设置
     */
    override suspend fun saveSystemConfiguration(systemConfiguration: SystemConfiguration) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_NEED_NOTIFICATION] = systemConfiguration.isNeedNotification
            prefs[Keys.IS_NEED_UPDATE_COURSE] = systemConfiguration.isNeedUpdateCourse
            prefs[Keys.LAST_UPDATED_COURSE_DATE] = TimeUtil.localDateTimeToTimestamp(
                systemConfiguration.lastUpdatedCourseDate)
        }
    }

    /**
     * 获取系统设置
     */
    override fun getSystemConfiguration(): Flow<SystemConfiguration> =
        dataStore.data.map {
            SystemConfiguration(
                isNeedNotification = it[Keys.IS_NEED_NOTIFICATION] ?: false,
                isNeedUpdateCourse = it[Keys.IS_NEED_UPDATE_COURSE] ?: false,
                lastUpdatedCourseDate = TimeUtil.fromTimestampToLocalDateTime(
                    it[Keys.LAST_UPDATED_COURSE_DATE])
                    ?: Const.NO_LAST_UPDATE_SELECTED_DATE
            )
        }

    /**
     * 清除系统设置
     */
    override suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}