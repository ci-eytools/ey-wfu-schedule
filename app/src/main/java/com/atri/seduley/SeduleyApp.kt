package com.atri.seduley

import android.app.Application
import com.atri.seduley.core.exception.GlobalExceptionHandler
import dagger.hilt.android.HiltAndroidApp
import java.util.TimeZone
import javax.inject.Singleton

@Singleton
@HiltAndroidApp
class SeduleyApp : Application() {

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

        // 固定时区
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
    }
}