package com.atri.seduley.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.data.local.datastore.entity.SystemConfInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息存储库实现
 */
@Singleton
class SystemDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val IS_NEED_NOTIFICATION = booleanPreferencesKey("is_need_notification")
        val IS_NEED_UPDATE_COURSE = booleanPreferencesKey("is_need_update_course")
        val LAST_UPDATED_COURSE_TIME = longPreferencesKey("last_updated_course_time")
    }

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfInfo) {
        dataStore.edit { prefs ->
            prefs[Keys.IS_NEED_NOTIFICATION] = systemConfiguration.isNeedNotification
            prefs[Keys.IS_NEED_UPDATE_COURSE] = systemConfiguration.isNeedUpdateCourse
            prefs[Keys.LAST_UPDATED_COURSE_TIME] = TimeUtil.localDateTimeToTimestamp(
                systemConfiguration.lastUpdatedCourseDate)
        }
    }

    /** 获取系统设置信息 */
    fun getSystemConfInfo(): Flow<SystemConfInfo> =
        dataStore.data.map {
            SystemConfInfo(
                isNeedNotification = it[Keys.IS_NEED_NOTIFICATION] ?: false,
                isNeedUpdateCourse = it[Keys.IS_NEED_UPDATE_COURSE] ?: false,
                lastUpdatedCourseDate = TimeUtil.fromTimestampToLocalDateTime(
                    it[Keys.LAST_UPDATED_COURSE_TIME]
                )
                    ?: Const.NO_LAST_UPDATE_SELECTED_DATE
            )
        }

    /** 清除系统设置信息 */
    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}