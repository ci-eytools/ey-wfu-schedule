package com.atri.seduley.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 系统设置信息存储库实现
 */
@Singleton
class SystemDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val IS_NEED_NOTIFICATION = booleanPreferencesKey("is_need_notification")
        val IS_NEED_UPDATE_COURSE = booleanPreferencesKey("is_need_update_course")
    }

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity) {
        dataStore.edit {
            it[Keys.IS_NEED_NOTIFICATION] = systemConfiguration.isNeedNotification
            it[Keys.IS_NEED_UPDATE_COURSE] = systemConfiguration.isNeedUpdateCourse
        }
    }

    /** 获取系统设置信息 */
    fun systemConfInfoFlow(): Flow<SystemConfEntity> =
        dataStore.data.map {
            SystemConfEntity(
                isNeedNotification = it[Keys.IS_NEED_NOTIFICATION] ?: false,
                isNeedUpdateCourse = it[Keys.IS_NEED_UPDATE_COURSE] ?: true
            )
        }

    /** 清除系统设置信息 */
    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}