package com.atri.seduley.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.atri.seduley.data.local.datastore.entity.SystemConfEntity
import com.atri.seduley.data.local.datastore.entity.TaskWay
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
        val NOTIFICATION_WAY = intPreferencesKey("notification_way")
        val UPDATE_COURSE_WAY = intPreferencesKey("update_course_way")
    }

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConfiguration: SystemConfEntity) {
        dataStore.edit {
            it[Keys.NOTIFICATION_WAY] = systemConfiguration.notificationWay.value
            it[Keys.UPDATE_COURSE_WAY] = systemConfiguration.updateCourseWay.value
        }
    }

    /** 获取系统设置信息 */
    fun systemConfInfoFlow(): Flow<SystemConfEntity> =
        dataStore.data.map {
            SystemConfEntity(
                notificationWay = TaskWay.fromValue(it[Keys.NOTIFICATION_WAY] ?: 0),
                updateCourseWay = TaskWay.fromValue(it[Keys.UPDATE_COURSE_WAY] ?: 0)
            )
        }

    /** 清除系统设置信息 */
    suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}