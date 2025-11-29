package com.atri.seduley.domain.usecase

import android.content.Context
import android.graphics.BitmapFactory
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
        val defColor = Const.DEFAULT_SEED_COLOR_INT
        val cover = File(context.cacheDir, Const.COVER_IMAGE_NAME)
        val newColorInt = cover.let {
            try {
                withContext(Dispatchers.IO) {
                    FileInputStream(it).use { stream ->
                        val bitmap = BitmapFactory.decodeStream(stream)
                        val cover = extractDominantColor(bitmap, defColor).toArgb()
                        bitmap.recycle()
                        cover
                    }
                }
            } catch (_: Exception) {
                defColor
            }
        }
        systemConfRepository.saveSeedColor(newColorInt)
    }

    /** 主题颜色流 */
    fun seedColorFlow(): StateFlow<Int> =  systemConfRepository.seedColorFlow()

    /** 保存系统设置信息 */
    suspend fun saveSystemConfInfo(systemConf: SystemConf): Result<Unit> = toReturn {
        systemConfRepository.saveSystemConfInfo(systemConf.toEntity())
    }

    /** 获取系统设置信息 */
    fun getSystemConfInfo(): Result<Flow<SystemConf>> = toReturnSync {
        systemConfRepository.systemConfInfoFlow().map { it.toDomain() }
    }

    /** 清除系统设置信息 */
    suspend fun clear(): Result<Unit> = toReturn { systemConfRepository.clear() }
}