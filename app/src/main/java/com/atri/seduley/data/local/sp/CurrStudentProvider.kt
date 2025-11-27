package com.atri.seduley.data.local.sp

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 当前用户存储库实现，基于 SharedPreferences
 */
@Singleton
class CurrStudentProvider @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_STUDENT_ID = "current_student_id"
    }

    private val _currStudentIdFlow = MutableStateFlow(getCurrStudentId())

    /** 对外暴露的当前用户流 */
    val seedCurrStudentId: StateFlow<String?> = _currStudentIdFlow.asStateFlow()

    /** 获取当前用户 */
    fun getCurrStudentId(): String? {
        return prefs.getString(KEY_CURRENT_STUDENT_ID, null)
    }

    /** 保存当前用户 */
    fun saveCurrStudentId(studentId: String) {
        _currStudentIdFlow.value = studentId
        prefs.edit(commit = false) {
            putString(KEY_CURRENT_STUDENT_ID, studentId)
        }
    }

    /** 清除当前用户 */
    fun clear() {
        _currStudentIdFlow.value = null
        prefs.edit(commit = false) {
            remove(KEY_CURRENT_STUDENT_ID)
        }
    }
}
