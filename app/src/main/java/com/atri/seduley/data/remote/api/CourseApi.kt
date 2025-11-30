package com.atri.seduley.data.remote.api

import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.network.url.ApiUrls
import okhttp3.OkHttpClient
import javax.inject.Inject

/**
 * 请求主页
 */
class CourseApi @Inject constructor(
    private val requestHelper: RequestHelper
) {

    /**
     * 获取课表页 html
     *
     * @param rq YYYY-MM-dd 日期字符串
     */
    suspend fun getCoursePageHTML(rq: String): String {
        return requestHelper.post(ApiUrls.COURSE_PAGE.toUrl(), mapOf("rq" to rq))
    }

    suspend fun getCoursePageHTML(rq: String, client: OkHttpClient): String {
        return requestHelper.post(
            url = ApiUrls.COURSE_PAGE.toUrl(),
            params = mapOf("rq" to rq),
            client = client
        )
    }
}