package com.atri.seduley.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.data.local.datastore.entity.UserCredential
import com.atri.seduley.data.local.datastore.security.CryptoManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户凭证存储库实现
 */
@Singleton
class UserCredentialDatastore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) {

    private object Keys {
        val STUDENT_ID = stringPreferencesKey("student_id")
        val ENCRYPT_PASSWORD = stringPreferencesKey("password_encrypted")
    }

    /**
     * 保存用户凭证
     */
    suspend fun saveCredential(credential: UserCredential) {
        dataStore.edit { prefs ->
            if (credential.studentId.isNotEmpty()) {
                prefs[Keys.STUDENT_ID] = credential.studentId
            }
            if (credential.password.isNotEmpty()) {
                prefs[Keys.ENCRYPT_PASSWORD] = cryptoManager.encrypt(credential.password)
            }
        }
    }

    /**
     * 获取学号
     */
    fun getStudentId(): Flow<String> =
        dataStore.data.map { prefs ->
            prefs[Keys.STUDENT_ID] ?: ""
        }

    private suspend fun getEncryptPassword(): String {
        val encryptPassword = dataStore.data.first()[Keys.ENCRYPT_PASSWORD]
        return encryptPassword ?: throw CredentialException()
    }

    /**
     * 在 lambda 中使用密码, 使用后 String 的引用消失, 且立即丢弃 ByteArray
     * 由于后端必须使用 String 类型的密码格式, 必须生成 String, 故为了方便使用直接在此处生成
     */
    suspend fun <T> login(block: suspend (String, String) -> T): T {
        val studentId = getStudentId().first()
        val encrypted = getEncryptPassword()
        val plain = cryptoManager.decrypt(encrypted)
        return try {
            block(studentId, String(plain, Charsets.UTF_8))
        } finally {
            plain.fill(0)
        }
    }

    /**
     * 清除登录凭证
     */
    suspend fun clear() {
        dataStore.edit {
            it.clear()
        }
    }
}