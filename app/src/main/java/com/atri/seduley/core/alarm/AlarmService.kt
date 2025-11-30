package com.atri.seduley.core.alarm

interface AlarmService {

    /** 创建定时闹钟 */
    fun schedule(config: AlarmConfig)

    /** 取消定时闹钟 */
    fun cancel(requestCode: Int)
}