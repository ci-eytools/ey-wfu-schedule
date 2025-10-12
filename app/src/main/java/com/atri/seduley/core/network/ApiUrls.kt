package com.atri.seduley.core.network

object ApiUrls {

    /**
     * GET
     */
    const val LOGIN = "https://jw.wfu.edu.cn/Logon.do?method=logon"

    /**
     * GET
     */
    const val CAPTCHA = "https://jw.wfu.edu.cn/verifycode.servlet?t=123456"

    /**
     * GET
     */
    const val SESS = "https://jw.wfu.edu.cn/Logon.do?method=logon&flag=sess"

    /**
     * GET
     */
    const val STUDENT_MAIN_PAGE = "https://jw.wfu.edu.cn/jsxsd/framework/xsMain.jsp"

    /**
     * POST
     * params={'rq': '2025-09-12'}
     */
    const val COURSE_PAGE = "https://jw.wfu.edu.cn/jsxsd/framework/main_index_loadkb.jsp"
}