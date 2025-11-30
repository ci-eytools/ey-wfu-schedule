package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * 获取验证码图片
 */
class CaptchaApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    suspend fun getCaptcha(): ByteArray {
        return requestHelper.postBytes(
            ApiUrls.CAPTCHA.toUrl(),
            mapOf("t" to System.currentTimeMillis().toString())
        )
    }

    suspend fun getCaptcha(client: OkHttpClient): ByteArray {
        return requestHelper.postBytes(
            url = ApiUrls.CAPTCHA.toUrl(),
            params = mapOf("t" to System.currentTimeMillis().toString()),
            client = client
        )
    }
}