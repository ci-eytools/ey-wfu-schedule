package com.atri.seduley.ui.theme.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "theme_prefs"
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

object ThemePreference {
    private val COVER_URI_KEY = stringPreferencesKey("cover_uri")
    private val SEED_COLOR_KEY = intPreferencesKey("seed_color")
    const val DEFAULT_SEED_COLOR_INT = 0xFF6200EE.toInt() // 默认颜色值

    suspend fun saveCoverUri(context: Context, uri: String) {
        context.dataStore.edit { it[COVER_URI_KEY] = uri }
    }

    fun coverUriFlow(context: Context): Flow<String?> =
        context.dataStore.data.map { it[COVER_URI_KEY] }

    suspend fun saveSeedColor(context: Context, color: Int) {
        context.dataStore.edit { it[SEED_COLOR_KEY] = color }
    }

    fun seedColorFlow(context: Context): Flow<Int> = // 返回非空 Int, 内部处理 null
        context.dataStore.data.map { preferences ->
            preferences[SEED_COLOR_KEY] ?: DEFAULT_SEED_COLOR_INT
        }
}