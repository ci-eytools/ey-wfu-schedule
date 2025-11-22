package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import com.atri.seduley.data.remote.entity.LoginReq
import javax.inject.Inject

/**
 * 登录请求
 */
class LoginApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    suspend fun login(req: LoginReq): String {
        return requestHelper.post(
            ApiUrls.LOGIN.toUrl(),
            params = mapOf(
                "userAccount" to req.studentId,
                "userPassword" to req.password,
                "RANDOMCODE" to req.captcha,
                "encoded" to req.encoded
            )
        )
    }
}