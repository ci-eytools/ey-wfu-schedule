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
 * 主题颜色存储库实现，基于 SharedPreferences
 */
@Singleton
class SystemProvider @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("system_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SEED_COLOR = "seed_color"
        private const val KEY_DEFAULT_SPLASH = "is_default_splash"
    }

    private val _seedColorFlow = MutableStateFlow(
        prefs.getInt(KEY_SEED_COLOR, Const.DEFAULT_SEED_COLOR_INT)
    )

    val seedColorFlow: StateFlow<Int> = _seedColorFlow.asStateFlow()

    fun getSeedColor(): Int {
        return prefs.getInt(KEY_SEED_COLOR, Const.DEFAULT_SEED_COLOR_INT)
    }

    fun saveSeedColor(color: Int) {
        _seedColorFlow.value = color
        prefs.edit(commit = false) {
            putInt(KEY_SEED_COLOR, color)
        }
    }

    fun isDefaultSplash(): Boolean {
        return prefs.getBoolean(KEY_DEFAULT_SPLASH, true)
    }

    fun saveDefaultSplash(isDefault: Boolean) {
        prefs.edit(commit = false) {
            putBoolean(KEY_DEFAULT_SPLASH, isDefault)
        }
    }
}
