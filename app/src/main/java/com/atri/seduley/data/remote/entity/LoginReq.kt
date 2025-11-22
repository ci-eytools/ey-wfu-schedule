package com.atri.seduley.data.remote.entity

data class LoginReq(
    val studentId: String,
    val password: String,
    val captcha: String,
    val encoded: String
)