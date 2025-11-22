package com.atri.seduley.core.exception

/**
 * 凭证异常
 */
class CredentialException(
    override val message: String = "用户凭据为空"
) : BaseException(message = message)