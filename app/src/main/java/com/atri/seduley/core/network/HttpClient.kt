package com.atri.seduley.core.network

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy

object HttpClient {
    val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(JavaNetCookieJar(CookieManager().apply {
                setCookiePolicy(CookiePolicy.ACCEPT_ALL)
            }))
            .build()
    }
}