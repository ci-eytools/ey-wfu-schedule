package com.atri.seduley.data.local.sp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.atri.seduley.core.util.Const
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 应用配置存储
 */
@Singleton
class SettingsProvider @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SEED_COLOR = "seed_color"
        private const val KEY_SPLASH_DURATION = "splash_duration"
    }

    private val _seedColorFlow = MutableStateFlow(
        prefs.getInt(KEY_SEED_COLOR, Const.DEFAULT_SEED_COLOR_INT)
    )

    val seedColorFlow: StateFlow<Int> = _seedColorFlow.asStateFlow()

    fun saveSeedColor(color: Int) {
        _seedColorFlow.value = color
        prefs.edit(commit = false) { putInt(KEY_SEED_COLOR, color) }
    }

    private val _splashDurationFlow = MutableStateFlow(
        prefs.getInt(KEY_SPLASH_DURATION, Const.DEFAULT_SPLASH_DURATION)
    )

    val splashDurationFlow: StateFlow<Int> = _splashDurationFlow.asStateFlow()

    fun saveSplashDuration(durationMs: Int) {
        _splashDurationFlow.value = durationMs
        prefs.edit(commit = false) { putInt(KEY_SPLASH_DURATION, durationMs) }
    }

    fun getSplashDuration(): Int {
        return prefs.getInt(KEY_SPLASH_DURATION, Const.DEFAULT_SPLASH_DURATION)
    }
}