package com.atri.seduley.data.local.datastore.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64
import javax.inject.Inject

/**
 * 使用 Android KeyStore 安全管理 AES/GCM 密钥
 */
class CryptoManager @Inject constructor() {

    private val keyAlias = "seduley_aes_key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private val key: SecretKey by lazy {
        if (keyStore.containsAlias(keyAlias)) {
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            createKey()
        }
    }

    private fun createKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        val parameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }

    private fun newCipher(): Cipher = Cipher.getInstance("AES/GCM/NoPadding")

    fun encrypt(plainText: String): String {
        val cipher = newCipher()
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(iv + encrypted, Base64.NO_WRAP)
    }

    fun decrypt(encryptedText: String): ByteArray {
        return try {
            val decoded = Base64.decode(encryptedText, Base64.NO_WRAP)
            val iv = decoded.copyOfRange(0, 12)
            val cipherText = decoded.copyOfRange(12, decoded.size)
            val spec = GCMParameterSpec(128, iv)
            val cipher = newCipher()
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            cipher.doFinal(cipherText)
        } catch (_: Exception) {
            ByteArray(0)
        }
    }
}