package com.atri.seduley.core.exception

/**
 * 登录异常
 */
class LoginException(
    override val message: String = "登录失败, 请稍后重试"
) : BaseException(message = message)
