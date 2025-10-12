package com.atri.seduley.feature.course.domain.repository

import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO

interface BaseInfoRepository {

    /**
     * 加载数据
     */
    suspend fun init()

    /**
     * 保存基础信息
     */
    suspend fun saveBaseInfo(baseInfo: BaseInfoDTO)

    /**
     * 获取基础信息
     */
    suspend fun getBaseInfo(): BaseInfoDTO

    /**
     * 更新周课表拉取标志位
     */
    suspend fun updateEnterMark(enterMark: Int)
}