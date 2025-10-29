package com.atri.seduley

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.atri.seduley.navigation.AppNav
import com.atri.seduley.ui.theme.util.ThemeFromImageWithStore
import com.atri.seduley.ui.theme.util.ThemeState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val themeColor by ThemeState.seedColor.collectAsState()
            ThemeFromImageWithStore(seedColor = themeColor) {
                AppNav()
            }
        }

    }
}