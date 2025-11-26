package com.atri.seduley.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 封装网络请求
 */
@Singleton
class RequestHelper @Inject constructor(
    private val client: OkHttpClient
) {

    suspend fun get(
        url: String
    ): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }

    suspend fun postBytes(
        url: String,
        params: Map<String, String>
    ): ByteArray = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        client.newCall(request).execute().body?.bytes() ?: byteArrayOf()
    }

    suspend fun post(
        url: String,
        params: Map<String, String>
    ): String = withContext(Dispatchers.IO) {
        val formBody = FormBody.Builder().apply {
            params.forEach { (k, v) -> add(k, v) }
        }.build()
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()
        client.newCall(request).execute().body?.string() ?: ""
    }
}