package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Student

interface StudentRepository {

    /** 获取学生信息 */
    suspend fun getStudentInfo(studentId: String): Student

    /** 更新学生信息 */
    suspend fun updateStudentInfo(student: Student)

    /** 清除学生信息 */
    suspend fun clearStudentInfo(studentId: String)
}
