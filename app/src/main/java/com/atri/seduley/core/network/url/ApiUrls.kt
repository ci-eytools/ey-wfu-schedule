package com.atri.seduley.core.network.url

object ApiUrls {

    const val HOST = "https://jw.wfu.edu.cn/"

    const val LOGIN = "Logon.do?method=logon"
    const val CAPTCHA = "verifycode.servlet"
    const val SESS = "Logon.do?method=logon&flag=sess"
    const val STUDENT_MAIN_PAGE = "jsxsd/framework/xsMain.jsp"
    const val COURSE_PAGE = "jsxsd/framework/main_index_loadkb.jsp"

    /**
     * 验证码真实 URL —— 动态生成
     */
    fun captchaUrl(): String =
        HOST + CAPTCHA + "?t=" + System.currentTimeMillis()

    /**
     * 拼完整 URL
     */
    fun url(path: String): String = HOST + path
}