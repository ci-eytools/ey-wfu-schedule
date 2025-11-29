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
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.util.Const
import com.atri.seduley.data.local.sp.ThemeProvider
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.usecase.SystemConfUseCase
import com.atri.seduley.ui.navigation.AppNav
import com.atri.seduley.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var systemConfUseCase: SystemConfUseCase

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val seedColor by systemConfUseCase.seedColorFlow().collectAsState()
            AppTheme(seedColor) {
                AppNav()
            }
        }
    }
}