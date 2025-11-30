package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import okhttp3.OkHttpClient
import javax.inject.Inject

/** 主页 */
class HomeApi @Inject constructor(
    private val requestHelper: RequestHelper,
) {

    suspend fun getHome(): String {
        return requestHelper.get(ApiUrls.STUDENT_MAIN_PAGE.toUrl())
    }

    suspend fun getHome(client: OkHttpClient): String {
        return requestHelper.get(
            url = ApiUrls.STUDENT_MAIN_PAGE.toUrl(),
            client = client
        )
    }
}