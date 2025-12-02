package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    fun observeStudentByStudentId(studentId: Long): Flow<StudentEntity?>

    fun observeSemesterByStudentId(studentId: Long): Flow<SemesterEntity?> {
        return observeStudentByStudentId(studentId).map { it?.semester }
    }

    @Query("SELECT studentId FROM students")
    suspend fun getStudentIds(): List<Long>

    @Query("SELECT studentId FROM students")
    fun observeStudentIds(): Flow<List<Long>>

    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun clearStudent(studentId: Long)

    @Query(
        """
        UPDATE students 
        SET 
            startDate = -1, 
            endDate = -1, 
            totalWeeks = -1, 
            courseUpdatedAt = :courseUpdatedAt
        WHERE studentId = :studentId
    """
    )
    suspend fun clearSemester(studentId: Long, courseUpdatedAt: LocalDateTime)

    @Query(
        """
        UPDATE students 
        SET 
            startDate = -1, 
            endDate = -1, 
            totalWeeks = -1, 
            courseUpdatedAt = :courseUpdatedAt
    """
    )
    suspend fun clearAllSemester(courseUpdatedAt: LocalDateTime)

    @Query("SELECT courseUpdatedAt FROM students WHERE studentId = :studentId")
    fun observeUpdateTime(studentId: Long): Flow<LocalDateTime?>

    @Query("SELECT * FROM students")
    fun observeStudents(): Flow<List<StudentEntity>>

    @Query("UPDATE students SET nickname = :nickname WHERE studentId = :studentId")
    suspend fun updateNickname(studentId: Long, nickname: String)

}