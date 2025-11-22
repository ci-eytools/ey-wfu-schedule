package com.atri.seduley.core.network.url

enum class ApiUrls(val path: String) {
    LOGIN("Logon.do?method=logon"),
    CAPTCHA("verifycode.servlet"),
    SESS("Logon.do?method=logon&flag=sess"),
    STUDENT_MAIN_PAGE("jsxsd/framework/xsMain.jsp"),
    COURSE_PAGE("jsxsd/framework/main_index_loadkb.jsp");

    companion object {
        const val HOST = "https://jw.wfu.edu.cn/"
    }

    fun toUrl(): String = HOST + path
}
