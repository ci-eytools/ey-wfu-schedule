package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.model.Student
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface StudentRepository {

    /** 观察学期信息 */
    fun observeSemester(studentId: Long): Flow<Semester?>

    /** 观察所有学生信息 */
    fun observeStudent(): Flow<List<Student>>

    /** 获取所有 id */
    suspend fun getAllStudentId(): List<Long>

    /** 更新昵称 */
    suspend fun updateNickname(studentId: Long, nickname: String)

    /** 清除学生 */
    suspend fun clearStudent(studentId: Long)

    /** 更新课表更新时间 */
    suspend fun updateCourseUpdateAt(studentId: Long, updateTime: LocalDateTime)

    /** 观察更新时间 */
    fun observeUpdateTime(studentId: Long): Flow<LocalDateTime?>

    // TODD
    suspend fun add(student: Student)
}
