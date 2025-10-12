package com.atri.seduley.core.exception

class LoginException(
    override val message: String = "登录失败, 请稍后重试"
) : BaseException(message = message)
