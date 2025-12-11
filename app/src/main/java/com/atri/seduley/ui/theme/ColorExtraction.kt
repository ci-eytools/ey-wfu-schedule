package com.atri.seduley.ui.theme

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val FALLBACK_COLOR = 0xFF777777.toInt()

suspend fun extractDominantColor(
    bitmap: Bitmap,
    defaultColor: Int = FALLBACK_COLOR
): Color = withContext(Dispatchers.Default) {

    val palette = Palette.from(bitmap).generate()

    val intColor = palette.getDominantColor(defaultColor)
    Color(intColor)
}