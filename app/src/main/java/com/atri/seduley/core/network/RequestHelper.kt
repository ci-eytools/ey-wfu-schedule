package com.atri.seduley.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Headers.Companion.toHeaders
import okhttp3.Request

/**
 * 封装网络请求
 */
object RequestHelper {

    suspend fun get(
        url: String,
        headers: Map<String, String> = NetworkUtils.defaultHeaders()
    ): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .headers(headers.toHeaders())
            .build()
        HttpClient.client.newCall(request).execute().body?.string() ?: ""
    }

    suspend fun getBytes(
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
        HttpClient.client.newCall(request).execute().body?.bytes() ?: byteArrayOf()
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
        HttpClient.client.newCall(request).execute().body?.string() ?: ""
    }
}