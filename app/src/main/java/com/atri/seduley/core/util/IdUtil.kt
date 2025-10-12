package com.atri.seduley.core.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.github.yitter.contract.IdGeneratorOptions
import com.github.yitter.idgen.YitIdHelper
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 全局唯一 ID 生成工具
 */
object IdUtil {

    private val isInitialized = AtomicBoolean(false)

    fun init(context: Context) {
        if (isInitialized.get()) return

        val workerId: Short = (getSafeDeviceId(context).hashCode() and 0xFF).toShort() // 0~255

        val options = IdGeneratorOptions(workerId).apply {
            BaseTime = 1700000000000L
            WorkerIdBitLength = 8
            SeqBitLength = 6
            MinSeqNumber = 5
        }

        YitIdHelper.setIdGenerator(options)
        isInitialized.set(true)
    }

    fun nextId(): Long {
        return YitIdHelper.nextId()
    }

    @SuppressLint("HardwareIds")
    private fun getSafeDeviceId(context: Context): String {
        return try {
            // 使用 Android ID
            val androidId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            androidId ?: Build.ID ?: "default"
        } catch (_: Exception) {
            "default"
        }
    }
}
