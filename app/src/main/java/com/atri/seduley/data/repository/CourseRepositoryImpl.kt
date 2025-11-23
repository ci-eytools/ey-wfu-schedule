package com.atri.seduley.data.repository

import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.remote.api.CourseApi
import com.atri.seduley.domain.model.Course
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.CourseRepository
import org.jsoup.Jsoup
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/**
 * 课表相关
 */
class CourseRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao,
    private val courseApi: CourseApi
) : CourseRepository {

    /** 更新课表 */
    override suspend fun updateCourse(studentId: String, courses: List<Course>) {
        studentDao.updateCourses(studentId, courses.map { it.toEntity() })
    }

    /** 从本地获取课表 */
    override suspend fun getCoursesFromDB(studentId: String): List<Course> {
        return studentDao.getCoursesByStudentId(studentId).map { it -> it.toDomain() }
    }

    /** 从远端获取本学期所有课表 */
    override suspend fun getCoursesFromRemote(studentId: String): List<Course> {
        val courses = mutableListOf<Course>()
        val monDate = LocalDate.now().toMonday()
        var semester = studentDao.getSemesterByStudentId(studentId)
        if (semester == null) {
            val html = courseApi.getCoursePageHTML(monDate.formatter())
            semester = parseSemesterInfo(html)
            // 本地为空就更新一下数据
            studentDao.updateSemester(studentId, semester)
        }
        val startDate = semester.startDate
        // 每次循环按周自增
        for (i in 0..semester.totalWeeks) {
            val date = startDate.plusWeeks(i.toLong())
            val html = courseApi.getCoursePageHTML(date.formatter())
            courses.addAll(parseCourseHtml(date, html))
        }
        return courses
    }


    /** 清除课表 */
    override suspend fun clearCourses(studentId: String) {
        studentDao.clearCoursesByStudentId(studentId)
    }

    /** 清除所有课表 */
    override suspend fun clearAllCourses() {
        studentDao.clearAllCourses()
    }

    /**
     * 解析课表页面
     *
     * @param monDate 归一化到周一的日期
     * @param html 课表页 html
     */
    private fun parseCourseHtml(
        monDate: LocalDate,
        html: String
    ): List<Course> {
        val courses = mutableListOf<Course>()
        val doc = Jsoup.parse(html)
        val table = doc.selectFirst("table.kb_table") ?: return mutableListOf()
        val rows = table.select("tbody tr")

        for ((rowIdx, row) in rows.withIndex()) {
            val cells = row.select("td")

            // col 0 = 节次说明，跳过
            for (colIdx in 1 until cells.size) {
                val p = cells[colIdx].selectFirst("p") ?: continue
                val title = p.attr("title")
                if (title.isBlank()) continue

                val lines = title.splitHtmlLines()

                var name = ""
                var credit = 0
                var type = ""
                var weekly = 0
                var location = ""

                for (line in lines) {
                    when {
                        "课程名称" in line ->
                            name = line.substringAfter("：").trim()

                        "课程学分" in line ->
                            credit =
                                ((line.substringAfter("：").toDoubleOrNull() ?: 0.0) * 100).toInt()

                        "课程属性" in line ->
                            type = line.substringAfter("：").trim()

                        "上课时间" in line -> {
                            // 提取周次数字
                            weekly = Regex("\\d+").find(line)?.value?.toInt() ?: 0
                        }

                        "上课地点" in line ->
                            location = line.substringAfter("：").trim()
                    }
                }

                val date = monDate.weekDate(weekly, colIdx)

                courses.add(
                    Course(
                        name = name,
                        credit = credit,
                        type = type,
                        location = location,
                        date = date,
                        weekly = weekly,
                        dayOfWeek = colIdx,
                        section = rowIdx + 1
                    )
                )
            }
        }

        return courses
    }

    /**
     * 解析当前周与总周数
     *
     * @param html 页面 HTML
     * @return 包含当前周和总周数
     * @throws IllegalArgumentException 页面解析失败时抛出
     */
    private fun parseSemesterWeekInfo(html: String): Pair<Int, Int> {
        val doc = Jsoup.parse(html)
        val div = doc.getElementById("li_showWeek")
            ?: throw IllegalArgumentException("未找到学期周数节点")

        val span = div.selectFirst("span.main_text.main_color")
            ?: throw IllegalArgumentException("未找到当前周节点")

        // 提取数字 例如："第12周" -> 12
        val currentWeek = Regex("\\d+").find(span.text())?.value?.toInt()
            ?: throw IllegalArgumentException("解析当前周失败")

        // 总周数在 span 后文本中，例如 "/21周"
        val totalWeeks = Regex("\\d+").find(div.ownText())?.value?.toInt()
            ?: throw IllegalArgumentException("解析总周数失败")

        return Pair(currentWeek, totalWeeks)
    }

    /**
     * 根据页面 HTML 解析学期信息
     *
     * @param html 页面 HTML
     * @return Pair(startDate, endDate)
     */
    private fun parseSemesterInfo(html: String): SemesterEntity {
        val (currentWeek, totalWeeks) = parseSemesterWeekInfo(html)

        // 当前日期归一化到本周周一
        val monDate = LocalDate.now().toMonday()

        // 计算学期开始日期：当前周的周一 - (currentWeek - 1) 周
        val startDate = monDate.minusWeeks((currentWeek - 1).toLong())

        // 结束日期 = 开始日期 + (总周数 - 1) 周的周日
        val endDate = startDate.plusWeeks((totalWeeks - 1).toLong()).with(DayOfWeek.SUNDAY)

        return SemesterEntity(startDate, endDate, totalWeeks)
    }

    /** 归一化到当前周周一日期 */
    private fun LocalDate.toMonday(): LocalDate =
        this.with(DayOfWeek.MONDAY)

    /** 根据周次与星期数计算当日日期 */
    private fun LocalDate.weekDate(week: Int, dayOfWeek: Int): LocalDate =
        this.plusDays(((week - 1) * 7L) + (dayOfWeek - 1))

    /** 格式化日期为 yyyy-MM-dd */
    private fun LocalDate.formatter(): String =
        this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

    /** 根据 title 字符串解析 <br/> 段 */
    private fun String.splitHtmlLines(): List<String> {
        return this.split("<br/>").map { it.trim() }.filter { it.isNotEmpty() }
    }
}