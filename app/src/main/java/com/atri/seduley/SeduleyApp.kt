package com.atri.seduley

import android.app.Application
import com.atri.seduley.core.alarm.domain.repository.AlarmRepository
import com.atri.seduley.core.exception.GlobalExceptionHandler
import com.atri.seduley.core.ml.CaptchaModel
import com.atri.seduley.core.util.Const
import com.atri.seduley.core.util.IdUtil
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

@HiltAndroidApp
class SeduleyApp : Application() {

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onCreate() {
        super.onCreate()

        // 获取系统默认的异常处理器
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // 设置全局异常处理器
        Thread.setDefaultUncaughtExceptionHandler(
            GlobalExceptionHandler(
                this,
                defaultHandler ?: Thread.UncaughtExceptionHandler { _, _ -> })
        )

        // 固定时区
        AndroidThreeTen.init(this)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))

        CaptchaModel.init(applicationContext)   // 初始化加载模型


        IdUtil.init(this)   // 初始化 Id 生成器

        CoroutineScope(Dispatchers.IO).launch {
            val allAlarmsNum = alarmRepository.getAllAlarmsCount()
            if (allAlarmsNum > Const.DELECT_ALARM_NUM) {
                val deleteNum =
                    alarmRepository.deleteCompletedScheduledAlarms()
                if (allAlarmsNum - deleteNum > Const.DELECT_ALARM_NUM) {
                    val deleteThanNum = alarmRepository.deleteAlarmsOlderThan(30)
                    if (deleteNum - (deleteThanNum.first + deleteThanNum.second) > Const.DELECT_ALARM_NUM) {
                        alarmRepository.deleteAllAlarms()
                    }
                }
            }
        }
    }
}