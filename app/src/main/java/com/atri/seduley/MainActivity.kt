package com.atri.seduley

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.atri.seduley.core.alarm.AlarmApi
import com.atri.seduley.navigation.AppNav
import com.atri.seduley.ui.theme.util.ThemeFromImageWithStore
import com.atri.seduley.ui.theme.util.ThemeState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var alarmApi: AlarmApi

    // 注册通知权限请求（Android 13+）
    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                Toast.makeText(this, "通知权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "未授予通知权限，提醒可能无法显示", Toast.LENGTH_LONG).show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
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