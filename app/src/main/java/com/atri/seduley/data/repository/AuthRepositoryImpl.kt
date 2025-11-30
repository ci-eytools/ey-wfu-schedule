package com.atri.seduley.data.repository

import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.util.AppLogger
import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.local.datastore.CredentialDataStore
import com.atri.seduley.data.local.datastore.entity.CredentialEntity
import com.atri.seduley.data.remote.api.CaptchaApi
import com.atri.seduley.data.remote.api.InitApi
import com.atri.seduley.data.remote.api.LoginApi
import com.atri.seduley.data.remote.api.SESSApi
import com.atri.seduley.data.remote.model.LoginReq
import com.atri.seduley.domain.ml.CaptchaRecognizer
import com.atri.seduley.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.OkHttpClient
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
    private val credentialDatastore: CredentialDataStore
) : AuthRepository {

    @Volatile
    private var isCurrIdLogin: Boolean = false

    /** 使用当前用户发起登录请求 */
    override suspend fun login(block: suspend () -> Unit) {
        if (isCurrIdLogin) return
        val studentId = credentialDatastore.observeCurrentStudentId().first()
        if (studentId == null) throw CredentialException("请先登录")
        credentialDatastore.login(studentId) { studentId, password ->
            loginAs(studentId, password, block)
        }
    }

    /** 使用指定已存在用户发起登录请求 */
    override suspend fun loginAs(studentId: Long, block: suspend () -> Unit) {
        credentialDatastore.login(studentId) { studentId, password ->
            loginAs(studentId, password, block)
        }
    }

    /** 使用指定用户发起登录，成功后自动保存 */
    override suspend fun loginAs(studentId: Long, password: String, block: suspend () -> Unit) {
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
                val encoded = getEncoded(studentId.toString(), password, sessResp)

                // --- 步骤 4: 提交最终登录表单 ---
                val loginResultResp = loginApi.login(
                    LoginReq(
                        studentId = studentId.toString(),
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
                    credentialDatastore.saveCredential(CredentialEntity(studentId, password))
                    credentialDatastore.saveCurrentStudentId(studentId)
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

    /** 使用指定已存在用户发起登录请求，使用传入的 OkHttpClient 实例 */
    override suspend fun loginAs(
        studentId: Long,
        isolatedClient: OkHttpClient,
        block: suspend () -> Unit
    ) {
        try {
            credentialDatastore.login(studentId) { studentId, password ->
                initApi.init(isolatedClient)
                for (i in 1..5) {
                    val bytes = captchaApi.getCaptcha(isolatedClient)
                    val captcha = captchaRecognizer.recognize(bytes)
                    val sessResp = sessApi.sess(isolatedClient)
                    val encoded = getEncoded(studentId.toString(), password, sessResp)
                    val loginResultResp = loginApi.login(
                        LoginReq(
                            studentId = studentId.toString(),
                            password = password,
                            captcha = captcha,
                            encoded = encoded
                        ),
                        isolatedClient
                    )
                    if (isCaptchaError(loginResultResp)) {
                        continue
                    }
                    if (isLoginSuccess(loginResultResp)) {
                        block()
                        break
                    }
                    if (isAccountOrPasswordError(loginResultResp)) {
                        break
                    }
                }
            }
        } catch (_: Throwable) { /* 高并发环境且为后台运行不做任何处理 */}
    }

    /** 观察当前登录用户 id */
    override fun observeCurrentStudentId() = credentialDatastore.observeCurrentStudentId()

    /** 获取当前登录用户 id */
    override suspend fun getCurrentStudentId(): Long? {
        return credentialDatastore.getCurrentStudentId()
    }

    /** 观察所有用户 id */
    override fun observeStudentIds(): Flow<List<Long>> {
        return studentDao.observeStudentIds()
    }

    /** 切换当前登录用户 */
    override suspend fun saveCurrentStudent(studentId: Long) {
        isCurrIdLogin = false
        credentialDatastore.saveCurrentStudentId(studentId)
    }


    /** 登出/删除当前用户 */
    override suspend fun logout() {
        val currentStudentId = credentialDatastore.observeCurrentStudentId().first()
        if (currentStudentId == null) {
            throw CredentialException("请先登录")
        }
        logoutAs(currentStudentId)
    }

    /** 登出/删除指定用户 */
    override suspend fun logoutAs(studentId: Long) {
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