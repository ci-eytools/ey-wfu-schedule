package com.atri.seduley.ui.theme

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun extractDominantColor(
    bitmap: Bitmap,
    defaultColor: Int
): Color = withContext(Dispatchers.Default) {

    val palette = Palette.from(bitmap).generate()
    val intColor = palette.getDominantColor(defaultColor)

    Color(intColor)
}
