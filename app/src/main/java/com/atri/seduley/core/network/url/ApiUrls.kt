package com.atri.seduley.core.network.url

enum class ApiUrls(val path: String) {

    /** POST */
    LOGIN("Logon.do?method=logon"),

    /** POST */
    CAPTCHA("verifycode.servlet"),

    /** GET */
    SESS("Logon.do?method=logon&flag=sess"),

    /**
     * GET
     */
    STUDENT_MAIN_PAGE("jsxsd/framework/xsMain_new.jsp"),

    /**
     * POST
     * params={'rq': '2025-09-12'}
     */
    COURSE_PAGE("jsxsd/framework/main_index_loadkb.jsp");

    companion object {
        const val HOST = "https://jw.wfu.edu.cn/"
    }

    fun toUrl(): String = HOST + path
}
