package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * 初始发起登录请求，固定 session，headers 等信息
 */
class InitApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    /** 使用默认的 OkHttpClient 初始化会话 */
    suspend fun init() {
        requestHelper.get(ApiUrls.LOGIN.toUrl())
    }

    /** 使用指定的 OkHttpClient 初始化会话，用于需要隔离会话的并发任务 */
    suspend fun init(client: OkHttpClient) {
        requestHelper.get(
            url = ApiUrls.LOGIN.toUrl(),
            client = client
        )
    }
}