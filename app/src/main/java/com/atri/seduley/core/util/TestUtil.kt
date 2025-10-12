package com.atri.seduley.core.util

import android.content.Context
import android.util.Log

fun checkAssetDatabase(context: Context, dbName: String) {
    val assetManager = context.assets
    try {
        // 列出 databases 文件夹里的所有内容
        val files = assetManager.list("databases") ?: emptyArray()
        Log.d("DB_CHECK", "Files in assets/databases/: ${files.joinToString()}")

        // 判断文件是否存在
        if (files.contains(dbName)) {
            Log.d("DB_CHECK", "✅ Found $dbName in assets/databases/")
        } else {
            Log.e("DB_CHECK", "❌ $dbName NOT found in assets/databases/")
        }
    } catch (e: Exception) {
        Log.e("DB_CHECK", "Error checking assets: ${e.message}", e)
    }
}