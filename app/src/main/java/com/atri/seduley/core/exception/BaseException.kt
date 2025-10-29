package com.atri.seduley.core.exception

/**
 * 基础异常
 */
open class BaseException(
    override val message: String = "Unknown Error"
) : Exception(message)
