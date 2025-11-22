package com.atri.seduley.core.network

import com.atri.seduley.core.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

/**
 * 封装网络请求
 */
class RequestHelper @Inject constructor(
    private val client: OkHttpClient
) {

    private var fixedHeaders: Map<String, String>? = null

    fun init(headers: Map<String, String>) {
        fixedHeaders = headers
    }

    suspend fun get(
        url: String,
        headers: Map<String, String>? = null
    ): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .headers(resolveHeaders(headers).toHeaders())
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }

    suspend fun postBytes(
        url: String,
        params: Map<String, String>,
        headers: Map<String, String> ?= null
    ): ByteArray = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .headers(resolveHeaders(headers).toHeaders())
            .build()
        client.newCall(request).execute().body?.bytes() ?: byteArrayOf()
    }

    suspend fun post(
        url: String,
        params: Map<String, String>,
        headers: Map<String, String> ?= null
    ): String = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .headers(resolveHeaders(headers).toHeaders())
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }

    /**
     * 优先使用传入的 headers，其次使用初始化的 headers，最后使用随机 headers
     */
    private fun resolveHeaders(headers: Map<String, String>?): Map<String, String> {
        return headers ?: fixedHeaders ?: NetworkUtils.randomHeaders()
    }
}