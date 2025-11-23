package com.atri.seduley.data.repository

import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.local.database.entity.Course
import com.atri.seduley.data.remote.api.CourseApi
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
        studentDao.updateCourses(studentId, courses)
    }

    /** 从本地获取课表 */
    override suspend fun getCoursesFromDB(studentId: String): List<Course> {
        return studentDao.getCoursesByStudentId(studentId)
    }

    /** 从远端获取课表
     *
     * @param date 返回该参数所在周的课表
     * @param courses 支持重复传入自动去重
     */
    override suspend fun getCoursesFromRemote(
        date: LocalDate,
        courses: MutableList<Course>
    ): MutableList<Course> {
        val monDate = date.toMonday()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dateStr = monDate.format(formatter)
        val html = courseApi.getCoursePageHTML(dateStr)
        return parseCourseHtml(monDate, html)
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
     * @param courses 支持重复传入自动去重
     */
    private fun parseCourseHtml(
        monDate: LocalDate,
        html: String,
        courses: MutableList<Course> = mutableListOf()
    ): MutableList<Course> {

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
                            credit = ((line.substringAfter("：").toDoubleOrNull() ?: 0.0) * 100).toInt()

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

    /** 归一化到当前周周一日期 */
    private fun LocalDate.toMonday(): LocalDate =
        this.with(DayOfWeek.MONDAY)

    /** 根据周次与星期数计算当日日期 */
    private fun LocalDate.weekDate(week: Int, dayOfWeek: Int): LocalDate =
        this.plusDays(((week - 1) * 7L) + (dayOfWeek - 1))

    /** 根据 title 字符串解析 <br/> 段 */
    private fun String.splitHtmlLines(): List<String> {
        return this.split("<br/>").map { it.trim() }.filter { it.isNotEmpty() }
    }
}