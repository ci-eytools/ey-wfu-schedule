package com.atri.seduley.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.atri.seduley.core.alarm.util.AppLogger

/**
 * 权限工具类 (检测、引导、关闭权限)
 */
object PermissionUtil {

    /** 检查普通权限是否已授予 */
    fun hasPermission(context: Context, permission: String): Boolean {
        val result = ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
        AppLogger.d("正在检测权限: $permission, 是否拥有权限: $result")
        return result
    }

    /** 请求普通权限（外部传入 launcher） */
    fun requestPermission(launcher: ActivityResultLauncher<String>, permission: String) {
        launcher.launch(permission)
    }

    /** 检查并引导用户开启通知权限 (Android 13+) */
    fun openNotificationPermission(context: Context, launcher: ActivityResultLauncher<String>?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                ?: openAppNotificationSettings(context)
        }
    }

    /** 精确闹钟权限单独检测 */
    fun hasExactAlarmPermission(context: Context): Boolean {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
        AppLogger.d("检测精确闹钟权限: $result")
        return result
    }

    /** 检查并引导用户忽略电池优化 (防止后台任务被系统杀死) */
    @SuppressLint("BatteryLife")
    fun openIgnoreBatteryOptimization(context: Context) {
        val pkg = context.packageName
        try {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:$pkg".toUri()
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Toast.makeText(
                context,
                "请允许应用忽略电池优化以保证提醒功能正常",
                Toast.LENGTH_LONG
            ).show()
        } catch (_: Exception) {
            Toast.makeText(context, "无法跳转到电池优化设置, 请手动开启", Toast.LENGTH_LONG).show()
            openAppSettings(context)
        }
    }

    fun hasIgnoreBatteryOptimization(context: Context): Boolean {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val pkg = context.packageName
        return pm.isIgnoringBatteryOptimizations(pkg)
    }


    /** 跳转到应用详情页 */
    fun openAppDetailsSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = "package:${context.packageName}".toUri()
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS))
        }
    }

    /** 打开当前应用的系统设置页面 */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** 打开通知权限设置界面 */
    @RequiresApi(Build.VERSION_CODES.O)
    fun openAppNotificationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            openAppSettings(context)
        }
    }

    /** 打开电池优化设置界面 */
    fun openBatteryOptimizationSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivity(intent)
        } catch (_: Exception) {
            openAppSettings(context)
        }
    }

    /** 打开精确闹钟权限设置界面 */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = "package:${context.packageName}".toUri()
                }
                context.startActivity(intent)
            } catch (_: Exception) {
                openAppSettings(context)
            }
        }
    }
}