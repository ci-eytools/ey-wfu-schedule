package com.atri.seduley.feature.setting.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.security.CryptoManager
import com.atri.seduley.feature.setting.domain.entity.UserCredential
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserCredentialRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val cryptoManager: CryptoManager
) : UserCredentialRepository {

    private var cachedStudentId: String? = null
    private var cachedEncryptPassword: String? = null

    override suspend fun init() {
        cachedStudentId = getStudentId()
        cachedEncryptPassword = getEncryptPassword()
    }

    private object Keys {
        val STUDENT_ID = stringPreferencesKey("studentId")
        val EncryptPassword = stringPreferencesKey("encryptPassword")
    }

    override suspend fun saveCredential(credential: UserCredential) {
        dataStore.edit { prefs ->
            if (!credential.studentId.isNullOrEmpty())
                credential.studentId.let { prefs[Keys.STUDENT_ID] = it }
            if (!credential.password.isNullOrEmpty())
                credential.password.let {
                    prefs[Keys.EncryptPassword] = cryptoManager.encrypt(it)
                }
        }
        cachedStudentId = credential.studentId
        cachedEncryptPassword = credential.password
    }

    override suspend fun getStudentId(): String {
        val studentId = cachedStudentId.takeIf { !it.isNullOrEmpty() }
            ?: dataStore.data.first()[Keys.STUDENT_ID]
        if (studentId.isNullOrEmpty()) throw CredentialException()
        return studentId
    }

    private suspend fun getEncryptPassword(): String {
        val encryptPassword = cachedEncryptPassword.takeIf { !it.isNullOrEmpty() }
            ?: dataStore.data.first()[Keys.EncryptPassword]
        if (encryptPassword.isNullOrEmpty()) throw CredentialException()
        return encryptPassword
    }

    /**
     * 在 lambda 中使用密码, 使用后 String 的引用消失, 且立即丢弃 ByteArray
     * 由于后端必须使用 String 类型的密码格式, 必须生成 String, 故为了方便使用直接在此处生成
     */
    override suspend fun <T> login(block: suspend (String, String) -> T): T {
        val studentId = getStudentId()
        val encrypted = getEncryptPassword()
        val plain = cryptoManager.decrypt(encrypted)
        return try {
            block(studentId, String(plain, Charsets.UTF_8))
        } finally {
            plain.fill(0)
        }
    }

    override suspend fun clearCredential() {
        dataStore.edit {
            it.clear()
        }
    }
}