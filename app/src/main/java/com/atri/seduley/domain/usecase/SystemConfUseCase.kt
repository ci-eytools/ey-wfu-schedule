package com.atri.seduley.domain.usecase

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.toArgb
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.core.util.Const
import com.atri.seduley.domain.model.SystemConf
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.SystemConfRepository
import com.atri.seduley.domain.result.SystemConfResult
import com.atri.seduley.ui.theme.extractDominantColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

data class SystemConfUseCase @Inject constructor(
    private val systemConfRepository: SystemConfRepository
) {

    /**
     * 根据 URI 更新封面图片的持久化存储，并从中提取、保存主题种子颜色。
     * 如果 URI 为 null，则重置为默认主题色和空封面 URI。
     */
    suspend fun updateCoverAndSeedColorInStore(context: Context, uri: Uri?) = toReturn {
        val newUri = uri?.toString() ?: ""
        val defColor = Const.DEFAULT_SEED_COLOR_INT
        val newColorInt = try {
            uri?.let {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)?.use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        extractDominantColor(bitmap, defColor).toArgb()
                    }
                }
            } ?: defColor
        } catch (_: IOException) {
            defColor
        }
        systemConfRepository.saveCoverUri(newUri)
        systemConfRepository.saveSeedColor(newColorInt)
    }

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConf: SystemConf) = toReturn {
        systemConfRepository.saveSystemConfInfo(systemConf.toEntity())
    }

    /** 获取系统设置信息 */
    suspend fun getSystemConfInfo(): SystemConfResult = SystemConfResult.Success(
        systemConfRepository.getSystemConfInfo().toDomain())

    /** 清除系统设置信息 */
    suspend fun clear() = toReturn { systemConfRepository.clear() }

    /** 快速处理异常 */
    private suspend fun toReturn(block: suspend () -> Unit): SystemConfResult {
        return try {
            block()
            SystemConfResult.Success()
        } catch (e: Exception) {
            AppLogger.e(e)
            SystemConfResult.UnknownError
        }
    }
}