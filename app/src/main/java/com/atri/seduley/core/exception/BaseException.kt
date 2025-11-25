package com.atri.seduley.core.exception

/**
 * 基础异常
 */
open class BaseException(
    override val message: String = "未知错误",
    val e: Throwable? = null
) : Exception(message)
