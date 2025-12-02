package com.atri.seduley.data.repository

import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.domain.model.Semester
import com.atri.seduley.domain.model.Student
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao
) : StudentRepository {

    /** 观察学期信息 */
    override fun observeSemester(studentId: Long): Flow<Semester?> {
        return studentDao.observeSemesterByStudentId(studentId).map { semester -> semester?.toDomain() }
    }

    /** 观察所有学生信息 */
    override fun observeStudent(): Flow<List<Student>> {
        return studentDao.observeStudents().map { students -> students.map { it.toDomain() } }
    }

    /** 获取所有 id */
    override suspend fun getAllStudentId(): List<Long> {
        return studentDao.getStudentIds()
    }

    /** 更新昵称 */
    override suspend fun updateNickname(studentId: Long, nickname: String) {
        studentDao.updateNickname(studentId, nickname)
    }

    /** 清除学生 */
    override suspend fun clearStudent(studentId: Long) {
        studentDao.clearStudent(studentId)
    }

    /** 观察更新时间 */
    override fun observeUpdateTime(studentId: Long): Flow<LocalDateTime?> {
        return studentDao.observeUpdateTime(studentId)
    }

    override suspend fun add(student: Student) {
        studentDao.insert(student.toEntity())
    }
}