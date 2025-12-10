package com.atri.seduley.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import com.atri.seduley.core.util.Const
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.SystemConfRepository
import com.atri.seduley.domain.result.Result
import com.atri.seduley.domain.result.toReturn
import com.atri.seduley.domain.result.toReturnSync
import com.atri.seduley.ui.theme.extractDominantColor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

data class SystemConfUseCase @Inject constructor(
    private val systemConfRepository: SystemConfRepository,
    @ApplicationContext private val context: Context
) {

    /**
     * 根据 cover 文件更新主题种子颜色。如果 cover 文件为 不存在，则重置为默认主题色
     */
    suspend fun updateSeedColorByCover(): Result<Unit> = toReturn {
        systemConfRepository.saveSeedColor(genSeedColor())
    }

    /** 主题颜色流 */
    fun seedColorFlow(): StateFlow<Int> = systemConfRepository.seedColorFlow()

    /** 保存开屏页持续时间 */
    fun saveSplashDuration(durationMs: Int) = toReturnSync {
        systemConfRepository.saveSplashDuration(durationMs)
    }

    /** 开屏页持续时间流 */
    fun splashDurationFlow(): StateFlow<Int> = systemConfRepository.splashDurationFlow()

    /** 获取开屏页持续时间 */
    fun getSplashDuration() = toReturnSync {
        systemConfRepository.getSplashDuration()
    }

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConf: SystemConf): Result<Unit> = toReturn {
        systemConfRepository.saveSystemConfInfo(systemConf.toEntity())
    }

    /** 观察系统设置信息 */
    fun observeSystemConfInfo(): Flow<SystemConf> {
        return systemConfRepository.systemConfInfoFlow().map { it.toDomain() }
    }

    /** 清除系统设置信息 */
    suspend fun clear(): Result<Unit> = toReturn { systemConfRepository.clear() }

    /** 生成主题种子颜色 */
    private suspend fun genSeedColor(): Int = withContext(Dispatchers.IO) {
        val defColor = Const.DEFAULT_SEED_COLOR_INT
        val coverJpgFile = File(context.cacheDir, Const.COVER_IMAGE_NAME + ".jpg")
        val coverGifFile = File(context.cacheDir, Const.COVER_IMAGE_NAME + ".gif")

        val targetFile = when {
            coverGifFile.exists() -> coverGifFile
            coverJpgFile.exists() -> coverJpgFile
            else -> null
        } ?: return@withContext defColor

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                val source = ImageDecoder.createSource(targetFile)
                val bitmap = ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                }

                // 裁剪成 16:9
                val width = bitmap.width
                val height = bitmap.height
                val targetRatio = 16f / 9f
                val cropWidth: Int
                val cropHeight: Int

                if (width.toFloat() / height > targetRatio) {
                    cropHeight = height
                    cropWidth = (height * targetRatio).toInt()
                } else {
                    cropWidth = width
                    cropHeight = (width / targetRatio).toInt()
                }

                val x = (width - cropWidth) / 2
                val y = (height - cropHeight) / 2
                val cropped = Bitmap.createBitmap(bitmap, x, y, cropWidth, cropHeight)

                val color = extractDominantColor(cropped, defColor).toArgb()
                bitmap.recycle()
                cropped.recycle()
                color
            } catch (_: Exception) {
                defColor
            }
        } else {
            // 安卓版本过低，直接整体生成
            genCommonSeedColor(targetFile)
        }
    }

    private suspend fun genCommonSeedColor(file: File): Int {
        val defColor = Const.DEFAULT_SEED_COLOR_INT
        if (file.exists()) {
            FileInputStream(file).use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream) ?: return defColor

                val color = extractDominantColor(bitmap, defColor).toArgb()
                bitmap.recycle()
                color
            }
        }
        return defColor
    }
}