package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Semester
import kotlinx.coroutines.flow.Flow

interface StudentRepository {

    /** 观察学期信息 */
    fun observeSemester(studentId: Long): Flow<Semester?>

    /** 清除学生 */
    suspend fun clearStudent(studentId: Long)
}
