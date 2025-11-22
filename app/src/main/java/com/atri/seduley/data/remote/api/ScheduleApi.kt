package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import javax.inject.Inject

/**
 * 请求主页
 */
class ScheduleApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    suspend fun getSchedule(): String {
        return requestHelper.get(ApiUrls.STUDENT_MAIN_PAGE.toUrl())
    }
}