package com.atri.seduley.core.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 全局后台任务使用
 */
@Singleton
class SystemCoroutineScope @Inject constructor() {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}