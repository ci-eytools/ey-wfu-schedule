package com.atri.seduley.domain.alarm // Or your preferred package

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.atri.seduley.core.util.SystemCoroutineScope
import com.atri.seduley.domain.usecase.TaskUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 接收系统启动完成的广播 (BOOT_COMPLETED).
 * 职责是: 触发一个UseCase来重新调度所有必要的闹钟.
 */
@AndroidEntryPoint
class BootCompletedReceiver: BroadcastReceiver() {

    @Inject
    lateinit var systemScope: SystemCoroutineScope
    @Inject
    lateinit var taskUseCase: TaskUseCase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        val pendingResult = goAsync()

        systemScope.scope.launch {
            try {
                taskUseCase.rescheduleAlarms()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
