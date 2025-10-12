package com.atri.seduley.core.exception

open class BaseException(
    override val message: String = "Unknown Error"
) : Exception(message)
