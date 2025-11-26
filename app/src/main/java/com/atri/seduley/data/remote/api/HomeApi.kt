package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import javax.inject.Inject

/** 主页 */
class HomeApi @Inject constructor(
    private val requestHelper: RequestHelper,
) {

    suspend fun getHome(): String {
        return requestHelper.get(ApiUrls.STUDENT_MAIN_PAGE.toUrl())
    }
}