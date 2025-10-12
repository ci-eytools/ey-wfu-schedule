package com.atri.seduley.ui.theme.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

object ThemeState {

    private val _seedColor = MutableStateFlow(0xFFC7BFFF.toInt())
    val seedColor = _seedColor

    /** 初始化主题色 */
    suspend fun init(context: Context) {
        val colorInt = withContext(Dispatchers.IO) {
            ThemePreference.seedColorFlow(context).firstOrNull() ?: 0xFFC7BFFF.toInt()
        }
        _seedColor.value = colorInt
    }
}