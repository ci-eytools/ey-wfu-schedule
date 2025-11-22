package com.atri.seduley.core.network.interceptors

import com.atri.seduley.BuildConfig
import com.atri.seduley.core.util.AppLogger
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
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

        if (request.headers.size > 0) {
            AppLogger.d("→ Request Headers:\n${request.headers}")
        }

        request.body?.let { body ->
            val buffer = Buffer()
            body.writeTo(buffer)
            val charset: Charset = body.contentType()?.charset(StandardCharsets.UTF_8)
                ?: StandardCharsets.UTF_8

            val bodyStr = buffer.readString(charset)
            AppLogger.d("→ Request Body: ${bodyStr.take(MAX_LOG_BODY_LENGTH)}")
        }

        val startNs = System.nanoTime()
        val response = chain.proceed(request)
        val tookMs = (System.nanoTime() - startNs) / 1_000_000

        AppLogger.i("← RESPONSE: ${response.code} (${tookMs}ms) ${response.request.url}")

        val responseBody = response.body
        val contentType = responseBody?.contentType()

        val rawBody = responseBody?.string() ?: ""

        AppLogger.d(
            "← Response Body: ${
                rawBody.take(MAX_LOG_BODY_LENGTH)
            }"
        )

        return response.newBuilder()
            .body(rawBody.toResponseBody(contentType))
            .build()
    }
}