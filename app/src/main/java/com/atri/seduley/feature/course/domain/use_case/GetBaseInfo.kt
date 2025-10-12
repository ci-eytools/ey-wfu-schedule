package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import javax.inject.Inject

class GetBaseInfo @Inject constructor(
    private val repository: BaseInfoRepository
) {

    suspend operator fun invoke() = repository.getBaseInfo()
}