package com.atri.seduley

import android.app.Application
import com.atri.seduley.core.exception.GlobalExceptionHandler
import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@HiltAndroidApp
class SeduleyApp : Application() {

    @Inject
    lateinit var requestHelper: RequestHelper


    override fun onCreate() {
        super.onCreate()
        // 获取系统默认的异常处理器
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                this,
                defaultHandler ?: Thread.UncaughtExceptionHandler { _, _ -> })
        )

        // 网络预热
        CoroutineScope(Dispatchers.IO).launch {
            try {
                requestHelper.get(ApiUrls.LOGIN.toUrl())
            } catch (_: Exception) { /* 预热失败无需处理 */ }
        }

        // 固定时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
    }
}