package com.atri.seduley.data.repository

import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.core.util.NetworkUtils
import com.atri.seduley.data.local.datastore.CredentialDatastore
import com.atri.seduley.data.local.datastore.entity.Credential
import com.atri.seduley.data.remote.api.CaptchaApi
import com.atri.seduley.data.remote.api.InitApi
import com.atri.seduley.data.remote.api.LoginApi
import com.atri.seduley.data.remote.api.SESSApi
import com.atri.seduley.data.remote.model.LoginReq
import com.atri.seduley.domain.ml.CaptchaRecognizer
import com.atri.seduley.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val initApi: InitApi,
    private val loginApi: LoginApi,
    private val sessApi: SESSApi,
    private val captchaApi: CaptchaApi,
    private val captchaRecognizer: CaptchaRecognizer,
    private val credentialDatastore: CredentialDatastore
) : AuthRepository {

    /** 使用当前用户发起登录请求 */
    override suspend fun login() {
        credentialDatastore.login { studentId, password ->
            loginAs(studentId, password)
        }
    }

    /** 使用指定已存在用户发起登录请求 */
    override suspend fun loginAs(studentId: String) {
        credentialDatastore.login(studentId) { studentId, password ->
            loginAs(studentId, password)
        }
    }

    /** 使用指定用户发起登录，成功后自动保存 */
    override suspend fun loginAs(studentId: String, password: String) {
        try {
            // 1.初始化登录，固定 session，headers
            initApi.init(NetworkUtils.randomHeaders())

            // 2.请求 sess
            val sessResp = sessApi.sess()

            // 3.构造 encoded
            val encoded = getEncoded(studentId, password, sessResp)

            // 最多尝试 5 次
            for (i in 1..5) {
                // 4.获取验证码图片
                val bytes = captchaApi.getCaptcha()

                // 5.送入模型预测
                val captcha = captchaRecognizer.recognize(bytes)
                AppLogger.d("第 $i 次识别验证码: $captcha")

                // 6.提交登录表单
                val loginResultResp = loginApi.login(
                    LoginReq(
                        studentId = studentId,
                        password = password,
                        captcha = captcha,
                        encoded = encoded
                    )
                )
                AppLogger.d("第 $i 次登录loginResultResp: $loginResultResp")
                if (isCaptchaError(loginResultResp)) {
                    AppLogger.d("第 $i 次登录失败: 验证码错误, 正在重试 $i/5")
                    continue
                }
                if (isLoginSuccess(loginResultResp)) {
                    AppLogger.d("第 $i 次登录loginResultResp: 登录成功")
                    credentialDatastore.saveCredential(Credential(studentId, password))
                    break
                }
                if (isAccountOrPasswordError(loginResultResp)) {
                    AppLogger.d("登录失败: 账号或密码错误")
                    throw CredentialException("账号或密码错误")
                }
                throw LoginException()
            }
        } catch (_: IOException) {
            throw NetworkException()
        } catch (_: Exception) {
            throw BaseException()
        }
    }

    /** 获取当前登录用户 id */
    override fun getCurrentStudentId(): Flow<String?> {
        return credentialDatastore.getCurrentStudent()
    }

    /** 获取所有用户 id */
    override fun getAllStudentId(): Flow<List<String>> {
        return credentialDatastore.getAllStudentId()
    }

    /** 登出/删除当前用户 */
    override suspend fun logout() {
        val currStudentId = getCurrentStudentId().first()
        if (currStudentId.isNullOrEmpty()) {
            throw CredentialException("请先登录")
        }
        credentialDatastore.clear(currStudentId)
    }

    /** 登出/删除指定用户 */
    override suspend fun logoutAs(studentId: String) {
        credentialDatastore.clear(studentId)
    }

    /** 登出/删除所有用户 */
    override suspend fun logoutAll() {
        credentialDatastore.clearAll()
    }


    /** 获取加密参数 */
    private fun getEncoded(account: String, password: String, sessResp: String): String {

        // 解析 scode 和 sxh
        val (scodeOrig, sxh) = sessResp.split("#")
        var scode = scodeOrig

        val code = "$account%%%$password"
        val encoded = StringBuilder()

        for (i in code.indices) {
            if (i < 20) {
                val count = sxh[i].digitToInt()
                encoded.append(code[i])
                encoded.append(scode.take(count))
                scode = scode.drop(count)
            } else {
                encoded.append(code.substring(i))
                break
            }
        }

        return encoded.toString()
    }

    /** 判断登录响应是否提示账号或密码错误 */
    private fun isAccountOrPasswordError(html: String): Boolean {
        val msg = getLoginText(html)
        return msg.contains("用户名或密码为空")
                || msg.contains("用户名或密码错误")
                || msg.contains("该帐号不存在或密码错误")
    }

    /** 判断登录响应是否提示验证码错误 */
    private fun isCaptchaError(html: String): Boolean {
        val msg = getLoginText(html)
        return msg.contains("验证码错误") || msg.contains("验证码无效")
    }

    /** 判断登录响应是否登录成功 */
    private fun isLoginSuccess(html: String): Boolean {
        return getLoginText(html).isEmpty()
    }

    /** 获取登录提示 */
    private fun getLoginText(html: String): String {
        val doc = Jsoup.parse(html)
        val msgTag = doc.getElementById("showMsg")
        return msgTag?.text()?.trim() ?: ""
    }
}