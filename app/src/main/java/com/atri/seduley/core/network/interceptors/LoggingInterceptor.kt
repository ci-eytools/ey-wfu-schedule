package com.atri.seduley.core.network.interceptors

import com.atri.seduley.BuildConfig
import com.atri.seduley.core.util.AppLogger
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class LoggingInterceptor : Interceptor {

    companion object {
        private const val MAX_LOG_BODY_LENGTH = 5_000
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }

        AppLogger.i("→ REQUEST: ${request.method} ${request.url}")

        val requestCookies = request.header("Cookie")
        if (requestCookies != null) {
            AppLogger.d("→ Request Cookies: $requestCookies")
        }

        if (request.headers.size > 0) {
            AppLogger.d("→ Request Headers:\n${request.headers}")
        }

        // --- 修复请求体处理 ---
        request.body?.let { body ->
            if (isPlainText(body.contentType()?.toString())) {
                val buffer = Buffer()
                body.writeTo(buffer)
                val charset: Charset = body.contentType()?.charset(StandardCharsets.UTF_8)
                    ?: StandardCharsets.UTF_8

                val bodyStr = buffer.readString(charset)
                AppLogger.d("→ Request Body: ${bodyStr.take(MAX_LOG_BODY_LENGTH)}")
            } else {
                AppLogger.d("→ Request Body: (Binary data, length=${body.contentLength()})")
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            AppLogger.e(message = "→ HTTP FAILED: $e")
            throw e
        }
        val tookMs = (System.nanoTime() - startNs) / 1_000_000

        AppLogger.i("← RESPONSE: ${response.code} (${tookMs}ms) ${response.request.url}")
        val responseCookies = response.headers("Set-Cookie")
        if (responseCookies.isNotEmpty()) {
            responseCookies.forEach { cookie ->
                AppLogger.d("← Response-Set-Cookie: $cookie")
            }
        }

        // --- 修复响应体处理 ---
        val responseBody = response.body
        if (responseBody != null && isPlainText(responseBody.contentType()?.toString())) {
            // 对于文本类型，可以读取并打印
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            val buffer = source.buffer

            val charset: Charset = responseBody.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8
            val bodyStr = buffer.clone().readString(charset)

            AppLogger.d("← Response Body: ${bodyStr.take(MAX_LOG_BODY_LENGTH)}")

            return response
        } else {
            AppLogger.d("← Response Body: (Binary data or empty body, length=${responseBody?.contentLength() ?: -1})")
        }

        return response
    }

    /**
     * 判断 Content-Type 是否为文本类型
     */
    private fun isPlainText(contentType: String?): Boolean {
        if (contentType == null) return false
        return contentType.contains("text") ||
                contentType.contains("json") ||
                contentType.contains("xml") ||
                contentType.contains("html") ||
                contentType.contains("x-www-form-urlencoded")
    }
}
