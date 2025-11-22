package com.atri.seduley.core.network.util

/**
 * 网络请求工具类
 */
object NetworkUtils {

    /**
     * 随机生成 User-Agent
     */
    fun randomUserAgent(): String {
        val version = "${(50..120).random()}.0.${(1000..5000).random()}.${(0..200).random()}"
        val platform = listOf("Windows NT 10.0; Win64; x64", "Macintosh; Intel Mac OS X 13_5_1", "Linux; Android 13; Pixel 7")
            .random()
        val browser = listOf("Chrome", "Edge", "Safari").random()
        return "Mozilla/5.0 ($platform) AppleWebKit/537.36 (KHTML, like Gecko) $browser/$version"
    }

    /**
     * 随机生成 headers
     */
    fun defaultHeaders(): Map<String, String> {
        return mapOf(
            "User-Agent" to randomUserAgent(),
            "Accept" to "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
            "Accept-Language" to "zh-CN,zh;q=0.9",
            "Connection" to "keep-alive"
        )
    }
}