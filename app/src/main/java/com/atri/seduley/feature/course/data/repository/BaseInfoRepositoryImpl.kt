package com.atri.seduley.feature.course.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * 基础信息存储库实现
 */
class BaseInfoRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BaseInfoRepository {

    private object Keys {
        val COLLEGE = stringPreferencesKey("college")
        val MAJOR = stringPreferencesKey("major")
        val START_DATE = longPreferencesKey("startDate")
        val END_DATE = longPreferencesKey("endDate")
        val ENTER_MARK = longPreferencesKey("enterMark")
    }

    /**
     * 保存基础信息
     */
    override suspend fun saveBaseInfo(baseInfo: BaseInfoDTO) {
        dataStore.edit { prefs ->
            prefs[Keys.COLLEGE] =
                baseInfo.college.takeIf { it.isNotEmpty() } ?: prefs[Keys.COLLEGE] ?: ""
            prefs[Keys.MAJOR] =
                baseInfo.major.takeIf { it.isNotEmpty() } ?: prefs[Keys.MAJOR] ?: ""
            prefs[Keys.START_DATE] = baseInfo.startDate
            prefs[Keys.END_DATE] = baseInfo.endDate
            prefs[Keys.ENTER_MARK] = baseInfo.enterMark.toLong()
        }
    }

    /**
     * 获取基础信息
     */
    override fun getBaseInfoDTO(): Flow<BaseInfoDTO> =
        dataStore.data.map {
            BaseInfoDTO(
                college = it[Keys.COLLEGE] ?: "",
                major = it[Keys.MAJOR] ?: "",
                startDate = it[Keys.START_DATE]
                    ?: TimeUtil.localDateToTimestamp(LocalDate.of(LocalDate.now().year, 1, 1)),
                endDate = it[Keys.END_DATE]
                    ?: TimeUtil.localDateToTimestamp(LocalDate.of(LocalDate.now().year, 12, 31)),
                enterMark = it[Keys.ENTER_MARK]?.toInt() ?: 0
            )
        }

    /**
     * 更新周课表拉取标志位
     */
    override suspend fun updateEnterMark(enterMark: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.ENTER_MARK] = enterMark.toLong()
        }
    }
}