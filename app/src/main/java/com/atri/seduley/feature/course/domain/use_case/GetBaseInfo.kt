package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import javax.inject.Inject

/**
 * 获取基础信息
 */
class GetBaseInfo @Inject constructor(
    private val repository: BaseInfoRepository
) {

    operator fun invoke() = repository.getBaseInfoDTO()
}