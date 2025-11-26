package com.atri.seduley.data.repository

import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.local.database.entity.StudentEntity
import com.atri.seduley.data.local.datastore.CredentialDatastore
import com.atri.seduley.data.local.datastore.entity.CredentialEntity
import com.atri.seduley.data.remote.api.CaptchaApi
import com.atri.seduley.data.remote.api.InitApi
import com.atri.seduley.data.remote.api.LoginApi
import com.atri.seduley.data.remote.api.SESSApi
import com.atri.seduley.data.remote.model.LoginReq
import com.atri.seduley.domain.ml.CaptchaRecognizer
import com.atri.seduley.domain.repository.AuthRepository
import org.jsoup.Jsoup
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val initApi: InitApi,
    private val loginApi: LoginApi,
    private val sessApi: SESSApi,
    private val captchaApi: CaptchaApi,
    private val captchaRecognizer: CaptchaRecognizer,
    private val studentDao: StudentDao,
    private val credentialDatastore: CredentialDatastore
) : AuthRepository {

    @Volatile
    private var isCurrIdLogin: Boolean = false

    /** 使用当前用户发起登录请求 */
    override suspend fun login() {
        if (isCurrIdLogin) return
        credentialDatastore.login { studentId, password ->
            loginAs(studentId, password) {}
        }
    }

    /** 使用指定已存在用户发起登录请求 */
    override suspend fun loginAs(studentId: String) {
        credentialDatastore.login(studentId) { studentId, password ->
            loginAs(studentId, password) {}
        }
    }

    /** 使用指定用户发起登录，成功后自动保存 */
    override suspend fun loginAs(studentId: String, password: String, block: suspend () -> Unit) {
        try {
            // --- 步骤 1: 初始化会话 ---
            // 访问登录页，目的是让 CookieJar 获取到初始的 JSESSIONID
            initApi.init()
            AppLogger.d("步骤1: 初始化会话完成")

            // 最多尝试 5 次
            for (i in 1..5) {
                // --- 步骤 2: 获取验证码图片 ---
                val bytes = captchaApi.getCaptcha()
                AppLogger.d("步骤2: 获取验证码图片完成")

                // 送入模型预测
                val captcha = captchaRecognizer.recognize(bytes)
                AppLogger.d("第 $i 次识别验证码: $captcha")

                // --- 步骤 3: 获取加密参数 (SESS) ---
                // 必须在获取验证码之后，提交登录之前
                val sessResp = sessApi.sess()
                AppLogger.d("步骤3: 获取SESS加密参数完成")

                // 构造 encoded
                val encoded = getEncoded(studentId, password, sessResp)

                // --- 步骤 4: 提交最终登录表单 ---
                val loginResultResp = loginApi.login(
                    LoginReq(
                        studentId = studentId,
                        password = password,
                        captcha = captcha,
                        encoded = encoded
                    )
                )
                AppLogger.d("步骤4: 第 $i 次提交登录表单")

                if (isCaptchaError(loginResultResp)) {
                    AppLogger.d("第 $i 次登录失败: 验证码错误, 正在重试 $i/5")
                    continue
                }
                if (isLoginSuccess(loginResultResp)) {
                    AppLogger.d("第 $i 次登录成功!")
                    studentDao.insert(StudentEntity(studentId = studentId))
                    credentialDatastore.saveCredential(CredentialEntity(studentId, password))
                    credentialDatastore.setCurrentStudent(studentId)
                    block()
                    return
                }
                if (isAccountOrPasswordError(loginResultResp)) {
                    AppLogger.d("登录失败: 账号或密码错误")
                    throw CredentialException("账号或密码错误")
                }
                AppLogger.d("第 $i 次登录失败: 未知响应，可能也是验证码问题，重试...")
            }
            throw LoginException("尝试5次后登录失败，请检查网络或稍后重试")

        } catch (_: IOException) {
            throw NetworkException()
        } catch (e: Exception) {
            AppLogger.e(e)
            throw BaseException(e = e)
        }
    }

    /** 获取当前登录用户 id */
    override suspend fun getCurrentStudentId(): String? {
        return credentialDatastore.getCurrentStudent()
    }

    /** 获取所有用户 id */
    override suspend fun getAllStudentId(): List<String> {
        return credentialDatastore.getAllStudentId()
    }

    /** 切换当前登录用户 */
    override suspend fun setCurrentStudent(studentId: String) {
        isCurrIdLogin = false
        credentialDatastore.setCurrentStudent(studentId)
    }


    /** 登出/删除当前用户 */
    override suspend fun logout() {
        val currStudentId = getCurrentStudentId().orEmpty()
        if (currStudentId.isEmpty()) {
            throw CredentialException("请先登录")
        }
        logoutAs(currStudentId)
    }

    /** 登出/删除指定用户 */
    override suspend fun logoutAs(studentId: String) {
        isCurrIdLogin = false
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