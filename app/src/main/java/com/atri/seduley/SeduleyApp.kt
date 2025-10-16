package com.atri.seduley

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.ml.CaptchaModel
import com.atri.seduley.core.util.IdUtil
import com.atri.seduley.core.util.checkAssetDatabase
import com.atri.seduley.feature.course.data.data_score.ClazzDatabase
import com.atri.seduley.feature.course.data.data_score.CourseDatabase
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class SeduleyApp : Application() {

    @Inject
    lateinit var baseInfoRepository: BaseInfoRepository

    @Inject
    lateinit var userCredentialRepository: UserCredentialRepository

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        CaptchaModel.init(applicationContext)   // 初始化加载模型

        IdUtil.init(this)   // 初始化 Id 生成器兜底

        CoroutineScope(Dispatchers.IO).launch {
            baseInfoRepository.init()
            try {
                userCredentialRepository.init()
            } catch (e: CredentialException) {
                Log.e("SeduleyApp", "CredentialException: ${e.message}")
            }
        }
    }
}

// 开发使用, 加载预制数据库
@Suppress("unused")
fun initDatabase(context: Context, dbName: String) {
    checkAssetDatabase(context, dbName)
    context.deleteDatabase(dbName)
    val db = when (dbName) {
        ClazzDatabase.DATABASE_NAME -> ClazzDatabase.getDatabase(context)
        CourseDatabase.DATABASE_NAME -> CourseDatabase.getDatabase(context)
        else -> null
    }
    db?.openHelper?.readableDatabase // 强制触发数据库创建
    val dbFile = context.getDatabasePath(dbName)
    Log.d(
        "DB_CHECK",
        "DB Path: ${dbFile.absolutePath}, " +
                "Exists: ${dbFile.exists()}, " +
                "Size: ${dbFile.length()}"
    )
}