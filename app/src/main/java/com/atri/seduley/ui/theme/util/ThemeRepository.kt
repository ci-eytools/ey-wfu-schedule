package com.atri.seduley.ui.theme.util

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThemeRepository {

    /**
     * 根据 URI 更新封面图片的持久化存储，并从中提取、保存主题种子颜色。
     * 如果 URI 为 null，则重置为默认主题色和空封面 URI。
     */
    suspend fun updateCoverAndSeedColorInStore(context: Context, uri: Uri?) {
        val newUri: String
        val newColorInt: Int
        if (uri == null) {
            newUri = "" // 保存空 URI
            newColorInt = ThemePreference.DEFAULT_SEED_COLOR_INT // 保存默认颜色
        } else {
            newUri = uri.toString()
            newColorInt = withContext(Dispatchers.IO) {
                try {
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        extractDominantColor(bitmap).toArgb()
                    } ?: ThemePreference.DEFAULT_SEED_COLOR_INT
                } catch (_: Exception) {
                    ThemePreference.DEFAULT_SEED_COLOR_INT
                }
            }
        }
        ThemePreference.saveCoverUri(context, newUri)
        ThemePreference.saveSeedColor(context, newColorInt)
        ThemeState.seedColor.value = newColorInt    // 触发主题更新
    }
}