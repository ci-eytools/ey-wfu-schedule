package com.atri.seduley.data.repository

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.domain.model.Student
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.StudentRepository
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val studentDao: StudentDao
) : StudentRepository {

    /** 获取学生信息 */
    override suspend fun getStudentInfo(studentId: String): Student {
        val studentInfoDB = studentDao.getStudentByStudentId(studentId)
        if (studentInfoDB == null) {
            throw CredentialException("该用户不存在")
        }
        return studentInfoDB.toDomain()
    }

    /** 更新学生信息 */
    override suspend fun updateStudentInfo(student: Student) {
        val studentInfoDB = studentDao.getStudentByStudentId(student.studentId)
        if (studentInfoDB == null) {
            throw CredentialException("该用户不存在")
        }
        studentDao.insert(student.toEntity().copy(
            id = studentInfoDB.id,
            courses = studentInfoDB.courses,
            createdAt = studentInfoDB.createdAt
        ))
    }

    /** 清除学生信息 */
    override suspend fun clearStudentInfo(studentId: String) {
        studentDao.clearByStudentId(studentId)
    }
}