package com.atri.seduley.ui.theme.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme

@Composable
fun ThemeFromImageWithStore(
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
