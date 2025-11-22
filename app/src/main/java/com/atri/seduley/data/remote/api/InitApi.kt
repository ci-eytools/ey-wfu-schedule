package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import javax.inject.Inject

/**
 * 初始发起登录请求，固定 session，headers 等信息
 */
class InitApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    suspend fun init(header: Map<String, String>) {
        requestHelper.init(header)
        requestHelper.get(ApiUrls.LOGIN.toUrl())
    }
}