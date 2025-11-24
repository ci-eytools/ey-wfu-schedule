package com.atri.seduley.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme

@Composable
fun AppTheme(
    seedColor: Int,
    content: @Composable () -> Unit
) {
    DynamicMaterialTheme(
        seedColor = Color(seedColor),
        useDarkTheme = isSystemInDarkTheme(),
        animate = true,
        content = content
    )
}
