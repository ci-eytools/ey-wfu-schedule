package com.atri.seduley.core.util

import android.util.Log
import com.atri.seduley.BuildConfig

/**
 * Logger
 *
 * 统一日志工具类。
 * Debug 模式下输出日志，Release 模式自动屏蔽。
 * 会自动推断调用类名作为 Tag。
 */
object AppLogger {

    private val ENABLE_LOG = BuildConfig.DEBUG

    /** 自动获取调用类名 */
    private fun tag(): String {
        val stackTrace = Throwable().stackTrace
        for (element in stackTrace) {
            val className = element.className
            if (!className.startsWith(AppLogger::class.java.name)) {
                return className.substringAfterLast('.')
            }
        }
        return "Logger"
    }

    /** Debug 级别日志 */
    fun d(message: String) {
        if (ENABLE_LOG) Log.d(tag(), message)
    }

    /** Info 级别日志 */
    fun i(message: String) {
        if (ENABLE_LOG) Log.i(tag(), message)
    }

    /** Warning 级别日志 */
    fun w(message: String) {
        if (ENABLE_LOG) Log.w(tag(), message)
    }

    /** Error 级别日志 */
    fun e(throwable: Throwable? = null, message: String? = null) {
        Log.e(tag(), message, throwable)
    }
}