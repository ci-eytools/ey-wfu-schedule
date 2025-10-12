package com.atri.seduley.core.exception

class CredentialException(
    override val message: String = "用户凭据为空"
) : BaseException(message = message)