package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Semester
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface StudentRepository {

    /** 观察学期信息 */
    fun observeSemester(studentId: Long): Flow<Semester?>

    /** 清除学生 */
    suspend fun clearStudent(studentId: Long)

    /** 观察更新时间 */
    fun observeUpdateTime(studentId: Long): Flow<LocalDateTime?>
}
