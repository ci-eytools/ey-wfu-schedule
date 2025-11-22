package com.atri.seduley.core.network

import com.atri.seduley.core.network.util.NetworkUtils
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
    suspend fun get(
        url: String,
        headers: Map<String, String> = NetworkUtils.defaultHeaders()
    ): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .headers(headers.toHeaders())
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }

    suspend fun postBytes(
        url: String,
        params: Map<String, String>,
        headers: Map<String, String> = NetworkUtils.defaultHeaders()
    ): ByteArray = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .headers(headers.toHeaders())
            .build()
        client.newCall(request).execute().body?.bytes() ?: byteArrayOf()
    }

    suspend fun post(
        url: String,
        params: Map<String, String>,
        headers: Map<String, String> = NetworkUtils.defaultHeaders()
    ): String = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .headers(headers.toHeaders())
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }
}