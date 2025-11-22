package com.atri.seduley.core.exception

import android.content.Context
import android.os.Build
import android.os.Process
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

/**
 * 全局未捕获异常处理器
 * 捕获未被 try-catch 的异常并写入日志文件，防止应用直接崩溃。
 */
class GlobalExceptionHandler(
    private val context: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler
) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val MAX_LOG_DIR_SIZE = 5 * 1024 * 1024 // 5 MB
        private const val CRASH_DIR_NAME = "crashes"
    }

    override fun uncaughtException(thread: Thread, e: Throwable) {
        AppLogger.e("发生未知错误!", e)

        try {
            writeExceptionToFile(e)
        } catch (ex: Exception) {
            AppLogger.e("写入日志文件失败", ex)
        }

        Process.killProcess(Process.myPid())
        exitProcess(10)
    }

    /**
     * 将异常堆栈信息转换为字符串
     */
    private fun getStackTrace(e: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    /**
     * 写入异常日志到内部存储
     */
    private fun writeExceptionToFile(e: Throwable) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(Date())
        val crashDir = File(context.filesDir, CRASH_DIR_NAME)
        if (!crashDir.exists()) crashDir.mkdirs()

        val crashLogFile = File(crashDir, "crash_${System.currentTimeMillis()}.log")

        crashLogFile.bufferedWriter().use { writer ->
            writer.appendLine("Timestamp: $timestamp")
            writer.appendLine("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            writer.appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            writer.appendLine("Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            writer.appendLine("\n--- Stack Trace ---")
            writer.appendLine(getStackTrace(e))
            writer.flush()
        }

        AppLogger.d("Crash log saved to: ${crashLogFile.absolutePath}")

        cleanupOldLogs(crashDir)
    }

    /**
     * 限制日志总大小，超过则删除最旧的文件
     */
    private fun cleanupOldLogs(crashDir: File) {
        val logFiles = crashDir.listFiles()?.sortedBy { it.lastModified() } ?: return
        var totalSize = logFiles.sumOf { it.length() }

        for (file in logFiles) {
            if (totalSize <= MAX_LOG_DIR_SIZE) break
            totalSize -= file.length()
            if (file.delete()) {
                AppLogger.i("Deleted old crash log: ${file.name}")
            }
        }
    }
}