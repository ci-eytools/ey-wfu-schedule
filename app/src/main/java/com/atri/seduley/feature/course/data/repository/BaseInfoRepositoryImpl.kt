package com.atri.seduley.feature.course.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import com.atri.seduley.feature.course.domain.entity.model.BaseInfo
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import kotlinx.coroutines.flow.first
import org.threeten.bp.LocalDate
import javax.inject.Inject

class BaseInfoRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BaseInfoRepository {

    private lateinit var cachedBaseInfo: BaseInfo

    override suspend fun init() {
        cachedBaseInfo = BaseInfo(
            id = 0,
            college = getBaseInfo().college,
            major = getBaseInfo().major,
            startDate = getBaseInfo().startDate,
            endDate = getBaseInfo().endDate,
            enterMark = getBaseInfo().enterMark.toLong()
        )
    }

    private object Keys {
        val COLLEGE = stringPreferencesKey("college")
        val MAJOR = stringPreferencesKey("major")
        val START_DATE = longPreferencesKey("startDate")
        val END_DATE = longPreferencesKey("endDate")
        val ENTER_MARK = longPreferencesKey("enterMark")
    }

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
        init()
    }

    override suspend fun getBaseInfo(): BaseInfoDTO {
        val prefs = dataStore.data.first()
        return BaseInfoDTO(
            college = prefs[Keys.COLLEGE] ?: "",
            major = prefs[Keys.MAJOR] ?: "",
            startDate = prefs[Keys.START_DATE]
                ?: TimeUtil.localDateToTimestamp(LocalDate.of(LocalDate.now().year, 1, 1)),
            endDate = prefs[Keys.END_DATE]
                ?: TimeUtil.localDateToTimestamp(LocalDate.of(LocalDate.now().year, 12, 31)),
            enterMark = prefs[Keys.ENTER_MARK]?.toInt() ?: 0
        )
    }

    override suspend fun updateEnterMark(enterMark: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.ENTER_MARK] = enterMark.toLong()
        }
        cachedBaseInfo.copy(enterMark = enterMark.toLong())
    }
}