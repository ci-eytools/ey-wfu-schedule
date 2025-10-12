package com.atri.seduley.ui.theme.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun extractDominantColor(bitmap: Bitmap): Color = withContext(Dispatchers.Default) {
    val palette = Palette.from(bitmap).generate()
    // 使用 ThemePreference 中的默认颜色作为 Palette 的备选
    val intColor = palette.getDominantColor(ThemePreference.DEFAULT_SEED_COLOR_INT)
    Color(intColor)
}