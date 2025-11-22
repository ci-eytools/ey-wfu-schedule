package com.atri.seduley.core.exception

/**
 * 网络异常
 */
class NetworkException(
    override val message: String = "网络连接错误"
) : BaseException(message = message)