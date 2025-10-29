package com.atri.seduley.feature.course.domain.repository

import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import kotlinx.coroutines.flow.Flow

interface BaseInfoRepository {

    /**
     * 保存基础信息
     */
    suspend fun saveBaseInfo(baseInfo: BaseInfoDTO)

    /**
     * 获取基础信息
     */
    fun getBaseInfoDTO(): Flow<BaseInfoDTO>

    /**
     * 更新周课表拉取标志位
     */
    suspend fun updateEnterMark(enterMark: Int)
}