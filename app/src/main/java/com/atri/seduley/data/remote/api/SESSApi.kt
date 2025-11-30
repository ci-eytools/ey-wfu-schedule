package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * 请求 SESS
 */
class SESSApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    suspend fun sess(): String {
        return requestHelper.get(ApiUrls.SESS.toUrl())
    }

    suspend fun sess(client: OkHttpClient): String {
        return requestHelper.get(
            url = ApiUrls.SESS.toUrl(),
            client = client
        )
    }
}