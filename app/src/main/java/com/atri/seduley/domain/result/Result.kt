package com.atri.seduley.domain.result

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.AppLogger

sealed interface Result<out T> {

    data class Success<T>(val value: T) : Result<T>

    data class Error(val msg: String? = null) : Result<Nothing>
}

suspend inline fun <T> toReturn(
    crossinline block: suspend () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CredentialException) {
        Result.Error(e.message)
    } catch (_: NetworkException) {
        Result.Error("请检查网络连接")
    } catch (e: Exception) {
        AppLogger.e(e)
        Result.Error()
    }
}

inline fun <T> toReturnSync(
    block: () -> T
): Result<T> {
    return try {
        Result.Success(block())
    } catch (e: CredentialException) {
        Result.Error(e.message)
    } catch (e: NetworkException) {
        Result.Error("请检查网络连接")
    } catch (e: Exception) {
        AppLogger.e(e)
        Result.Error()
    }
}


