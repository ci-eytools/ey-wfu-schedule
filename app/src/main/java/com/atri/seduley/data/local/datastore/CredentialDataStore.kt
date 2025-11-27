package com.atri.seduley.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.data.local.datastore.entity.CredentialEntity
import com.atri.seduley.data.local.datastore.security.CryptoManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户凭证存储库实现（支持多用户）
 * - 支持多用户凭证
 * - 记录当前登录用户
 * - 密码加密存储
 * - 明文密码使用后立即清零
 */
@Singleton
class CredentialDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) {

    // 每个 studentId 对应的键
    private fun studentIdKey(studentId: String) = stringPreferencesKey("student_id:$studentId")
    private fun encryptPasswordKey(studentId: String) =
        stringPreferencesKey("password_encrypted:$studentId")

    /** 保存用户凭证 */
    suspend fun saveCredential(credentialEntity: CredentialEntity) {
        val studentId = credentialEntity.studentId
        if (studentId.isEmpty()) return

        dataStore.edit { prefs ->
            prefs[studentIdKey(studentId)] = studentId
            if (credentialEntity.password.isNotEmpty()) {
                prefs[encryptPasswordKey(studentId)] =
                    cryptoManager.encrypt(credentialEntity.password)
            }
        }
    }

    /** 获取所有已保存的用户 id */
    suspend fun getAllStudentId(): List<String> {
        return dataStore.data.map { prefs ->
            prefs.asMap()
                .keys
                .mapNotNull { key ->
                    // 过滤 studentId key
                    val name = key.name
                    if (name.startsWith("student_id:")) {
                        name.removePrefix("student_id:")
                    } else null
                }
        }.first()
    }

    /**
     * 使用指定用户的密码进行登录操作
     * - lambda 内使用密码
     * - 使用后 ByteArray 被清零
     * - 默认使用当前用户
     */
    suspend fun <T> login(studentId: String?, block: suspend (String, String) -> T): T {
        val id = studentId ?: throw BaseException()
        if (id.isEmpty()) {
            throw CredentialException("用户凭证为空")
        }
        val encrypted = getEncryptPassword(studentId)
        val plain = cryptoManager.decrypt(encrypted)

        return try {
            block(studentId, String(plain, Charsets.UTF_8))
        } finally {
            plain.fill(0) // 清理内存中的明文
        }
    }

    /** 清除指定用户的凭证 */
    suspend fun clear(studentId: String) {
        dataStore.edit { prefs ->
            prefs.remove(studentIdKey(studentId))
            prefs.remove(encryptPasswordKey(studentId))
        }
    }

    /** 清除所有用户凭证 */
    suspend fun clearAll() {
        dataStore.edit { prefs -> prefs.clear() }
    }

    /** 获取指定用户的加密密码 */
    private suspend fun getEncryptPassword(studentId: String): String {
        val encrypted = dataStore.data.first()[encryptPasswordKey(studentId)]
        return encrypted ?: throw CredentialException("未持有该用户的登录凭证: $studentId")
    }
}
