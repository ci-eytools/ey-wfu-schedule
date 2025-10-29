package com.atri.seduley.feature.course.data.repository

import android.content.Context
import androidx.room.Transaction
import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.exception.BaseException
import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.core.exception.LoginException
import com.atri.seduley.core.exception.NetworkException
import com.atri.seduley.core.ml.CaptchaModel
import com.atri.seduley.core.network.ApiUrls
import com.atri.seduley.core.network.NetworkUtils
import com.atri.seduley.core.network.RequestHelper
import com.atri.seduley.core.util.IdUtil
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import com.atri.seduley.feature.course.domain.entity.dto.ParsedCourse
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.entity.model.Course
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import com.atri.seduley.feature.course.domain.repository.CourseRepository
import com.atri.seduley.feature.course.domain.repository.InitInfoRepository
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

/**
 * 初始化信息实现, 向服务器拉取相关信息
 */
class InitInfoRepositoryImpl @Inject constructor(
    private val courseRepository: CourseRepository,
    private val clazzRepository: ClazzRepository,
    private val baseInfoRepository: BaseInfoRepository,
    // debug 保存验证码图片时使用
    // @ApplicationContext private val context: Context
) : InitInfoRepository {

    private var isLoggedIn = false  // 全局登录标志

    private val loginLock = Any()   // 用于协程安全的同步

    private val headers = NetworkUtils.defaultHeaders()

    /**
     * 发起连接
     */
    override suspend fun connection(
        username: String,
        password: String,
        captcha: String?
    ) {
        initNetReq(username, password, NetworkUtils.defaultHeaders())
    }

    /**
     * 拉取所有初始信息
     */
    @Transaction
    override suspend fun enterOverallInfo(
        username: String,
        password: String,
        captcha: String?
    ): List<ParsedCourse> {
        initNetReq(username, password, headers)
        val start = TimeUtil.fromTimestampToLocalDate(
            baseInfoRepository.getBaseInfoDTO().first().startDate)
        val end = start.plusMonths(8)

        var result = mutableListOf<ParsedCourse>()
        for (date in generateSequence(start)
        { it.plusWeeks(1) }.takeWhile { !it.isAfter(end) }) {
            AppLogger.d("date: $date")
            result = parseCourseHtml(
                startDate = start,
                html = getCourseHTML(
                    date = date,
                    headers = headers
                ),
                result = result
            )
        }
        return result
    }

    /**
     * 获取指定日期的信息
     */
    override suspend fun enterInfo(
        username: String,
        password: String,
        date: LocalDate,
        captcha: String?
    ): List<ParsedCourse> {
        try {
            initNetReq(
                username = username,
                password = password,
                headers = headers
            )
        } catch (_: IOException) {
            throw NetworkException()
        } catch (e: LoginException) {
            throw e
        } catch (e: CredentialException) {
            throw e
        } catch (_: Exception) {
            throw BaseException()
        }


        // 7. 获取 course 信息
        val courseHtml = getCourseHTML(
            date = date,
            headers = headers
        )
        // 解析 course page
        val parsedCourses = parseCourseHtml(
            LocalDate.of(2025, 9, 1),
            courseHtml
        )
        AppLogger.d("courseHtml: $courseHtml")
        AppLogger.d("parsedCourses: $parsedCourses")

        return parsedCourses
    }

    override suspend fun getBaseInfo(
        username: String,
        password: String,
        captcha: String?
    ): BaseInfoDTO {
        initNetReq(username = username, password = password, headers = headers)

        val estimateStartDate = getEstimateStartDate()
        val estimateEndDate = estimateStartDate.plusMonths(6)

        var startDate: Long? = null
        var endDate: Long? = null

        // 查找第一周有课程的日期
        for (weekIndex in 0 until 52) {
            val date = estimateStartDate.plusWeeks(weekIndex.toLong())
            val parsedCourses = parseCourseHtml(
                startDate = estimateStartDate,
                html = getCourseHTML(date, headers)
            )
            AppLogger.d("date: $date, parsedCourses: $parsedCourses")
            if (parsedCourses.isNotEmpty()) {
                // 归一到周一
                startDate = TimeUtil.localDateToTimestamp(date.with(DayOfWeek.MONDAY))
                break
            }
        }

        // 查找最后一周有课程的日期
        for (weekIndex in 0 until 52) {
            val date = estimateEndDate.minusWeeks(weekIndex.toLong())
            val parsedCourses = parseCourseHtml(
                startDate = estimateStartDate,
                html = getCourseHTML(date, headers)
            )
            if (parsedCourses.isNotEmpty()) {
                // 归一到周日
                endDate = TimeUtil.localDateToTimestamp(date.with(DayOfWeek.SUNDAY))
                break
            }
        }

        AppLogger.d("startDate: $startDate, endDate: $endDate")
        if (startDate != null && endDate != null) {
            return BaseInfoDTO(startDate = startDate, endDate = endDate)
        }

        throw CredentialException("获取基础信息失败")
    }

    /**
     * 获取课表页面
     */
    private suspend fun getCourseHTML(date: LocalDate, headers: Map<String, String>): String {
        val formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")
        val dateStr = date.format(formatter)
        return RequestHelper.post(
            ApiUrls.COURSE_PAGE,
            mapOf("rq" to dateStr),
            headers
        )
    }

    /**
     * 部分更新(根据唯一键, course 忽略, clazz 覆盖)
     */
    @Transaction
    override suspend fun insertInfo(courses: List<Course>, clazzes: List<Clazz>) {
        courseRepository.insertCourses(courses)
        clazzRepository.insertClazzes(clazzes)
        val enterMark = baseInfoRepository.getBaseInfoDTO().first().enterMark
        val enterWeekly = clazzes[0].weekly
        baseInfoRepository.updateEnterMark(enterMark or (1 shl (enterWeekly - 1)))
    }

    /**
     * 全量更新(清空数据库再添加数据)
     */
    @Transaction
    override suspend fun insertOverallInfo(
        courses: List<Course>,
        clazzes: List<Clazz>,
        enterMark: Int
    ) {
        courseRepository.deleteAllCourses()
        courseRepository.insertCourses(courses)
        clazzRepository.deleteAllClazzes()
        clazzRepository.insertClazzes(clazzes)
        baseInfoRepository.updateEnterMark(enterMark)
    }

    /**
     * 登录流程
     */
    private suspend fun initNetReq(
        username: String,
        password: String,
        headers: Map<String, String>
    ) {
        // 如果已经登录，直接返回
        synchronized(loginLock) {
            if (isLoggedIn) {
                AppLogger.d("已登录, 直接返回")
                return
            }
        }

        // 1. 初始发起登录请求, 固定 session, headers 等信息
        RequestHelper.get(ApiUrls.LOGIN, headers)

        // 2. 请求 sess
        val sessResp = RequestHelper.get(ApiUrls.SESS, headers)

        // 构造 encoded
        val encoded = getEncoded(username, password, sessResp)

        var isLoginSuccess = false
        // 连续尝试五次
        for (i in 1..5) {
            // 3. 获取验证码图片
            val bytes = RequestHelper.getBytes(ApiUrls.CAPTCHA, headers)

            // 送入模型预测
            val captcha = recognizeCaptcha(bytes)
            AppLogger.d("第 $i 次识别验证码: $captcha")

            // 4. 提交登录表单
            val loginResultResp = RequestHelper.post(
                ApiUrls.LOGIN,
                params = mapOf(
                    "userAccount" to username,
                    "userPassword" to password,
                    "RANDOMCODE" to captcha,
                    "encoded" to encoded
                ), headers
            )

            AppLogger.d("第 $i 次登录loginResultResp: $loginResultResp")
            if (isCaptchaError(loginResultResp)) {
                AppLogger.d("第 $i 次登录失败: 验证码错误, 正在重试 $i/5")
                continue
            }
            if (isLoginSuccess(loginResultResp)) {
                isLoginSuccess = true
                AppLogger.d("第 $i 次登录loginResultResp: 登录成功")
                break
            }
            if (isAccountOrPasswordError(loginResultResp)) {
                AppLogger.d("登录失败: 账号或密码错误")
                throw CredentialException("账号或密码错误")
            }
            throw LoginException()
        }
        if (isLoginSuccess) {
            synchronized(loginLock) {
                isLoggedIn = true
            }
            // 6. 进入主页
            RequestHelper.get(ApiUrls.STUDENT_MAIN_PAGE, headers)
        }
    }

    /**
     * 识别验证码
     */
    private fun recognizeCaptcha(bytes: ByteArray): String {
        val inputBuffer = CaptchaModel.preprocessImage(bytes, width = 80, height = 40)
        val probabilityOutputs = Array(CaptchaModel.NUM_OUTPUTS) {
            FloatArray(CaptchaModel.NUM_CLASSES)
        }
        CaptchaModel.recognize(inputBuffer, probabilityOutputs)
        val captcha = CaptchaModel.decodeOutput(probabilityOutputs)
        return captcha
    }

    /**
     * 获取登录提示
     */
    private fun getMsgText(html: String): String {
        val doc = Jsoup.parse(html)
        val msgTag = doc.getElementById("showMsg")
        return msgTag?.text()?.trim() ?: ""
    }

    /**
     * 判断登录响应是否提示验证码错误
     */
    private fun isCaptchaError(html: String): Boolean {
        return getMsgText(html).contains("验证码错误")
                || getMsgText(html).contains("验证码无效")
    }

    /**
     * 判断登录响应是否提示账号或密码错误
     */
    private fun isAccountOrPasswordError(html: String): Boolean {
        val msgText = getMsgText(html)
        return msgText.contains("用户名或密码为空")
                || msgText.contains("用户名或密码错误")
                || msgText.contains("该帐号不存在或密码错误")
    }

    /**
     * 判断登录响应是否登录成功
     */
    private fun isLoginSuccess(html: String): Boolean {
        return getMsgText(html).isEmpty()
    }


    /**
     * 获取加密参数
     */
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


    /**
     * 解析课表页面
     *
     * @param result 解析结果, 支持重复传入自动去重
     */
    private fun parseCourseHtml(
        startDate: LocalDate,
        html: String,
        result: MutableList<ParsedCourse> = mutableListOf()
    ): MutableList<ParsedCourse> {
        val doc = Jsoup.parse(html)

        // 定位到课程表
        val table = doc.selectFirst("table.kb_table") ?: return mutableListOf()
        val rows = table.select("tbody tr")

        // 遍历每一行（对应节次）
        for ((rowIndex, row) in rows.withIndex()) {
            val cells = row.select("td")
            // 第一列是节次说明，跳过
            for (colIndex in 1 until cells.size) {
                val cell = cells[colIndex]
                val p = cell.selectFirst("p") ?: continue

                val title = p.attr("title") // 包含课程详情
                if (title.isBlank()) continue

                // 按 <br/> 切分
                val lines = title.split("<br/>")
                var name = ""
                var credit = 0
                var type = ""
                var weekly = 0
                var location = ""

                for (line in lines) {
                    when {
                        "课程名称" in line -> name = line.substringAfter("：")
                        "课程学分" in line -> credit =
                            ((line.substringAfter("：").toDoubleOrNull() ?: 0.0) * 100).toInt()


                        "课程属性" in line -> type = line.substringAfter("：")
                        "上课时间" in line -> {
                            weekly =
                                Regex("\\d+").find(
                                    line.substringAfter("：")
                                )?.value?.toInt() ?: 0
                        }

                        "上课地点" in line -> location = line.substringAfter("：")
                    }
                }

                val date = startDate.plusDays((weekly - 1) * 7 + (colIndex - 1).toLong())

                val course = ParsedCourse(
                    name = name,
                    credit = credit,
                    type = type,
                    location = location,
                    date = TimeUtil.localDateToTimestamp(date),
                    weekly = weekly,
                    dayOfWeek = colIndex,     // colIndex=1 → 星期一
                    section = rowIndex + 1    // 第几大节
                )
                result.add(course)
            }
        }
        return result
    }

    /**
     * 获取当前学期的估计起始日期
     */
    private fun getEstimateStartDate(): LocalDate {
        val year = LocalDate.now().year
        val estimateFirstHalfOfSchoolYear = LocalDate.of(year, 2, 1)
        val estimateSecondHalfOfSchoolYear = LocalDate.of(year, 8, 1)
        return if (LocalDate.now().isAfter(estimateSecondHalfOfSchoolYear))
            estimateSecondHalfOfSchoolYear
        else estimateFirstHalfOfSchoolYear
    }

    /**
     * 保存验证码图片, 用于 debug
     */
    @Suppress("unused")
    private fun saveByteArrayToInternalDir(
        context: Context,
        byteArray: ByteArray,
        fileNamePrefix: String = "captcha_debug"
    ): File? {
        val storageDir: File = context.getDir("my_debug_images", Context.MODE_PRIVATE)
        AppLogger.d("开始保存图片 size: ${byteArray.size} 到内部存储目录: ${storageDir.absolutePath}")

        if (!storageDir.exists() && !storageDir.mkdirs()) {
            AppLogger.e("无法创建或访问内部存储目录: $storageDir")
            return null
        }

        val timeStamp: String = IdUtil.nextId().toString()
        val imageFileName = "${fileNamePrefix}_${timeStamp}.jpg"
        val imageFile = File(storageDir, imageFileName)

        try {
            FileOutputStream(imageFile).use { fos ->
                fos.write(byteArray)
            }
            AppLogger.d("图片已保存到内部存储: ${imageFile.absolutePath}")
            return imageFile
        } catch (e: IOException) {
            AppLogger.e("保存图片到内部存储失败", e)
            return null
        }
    }
}