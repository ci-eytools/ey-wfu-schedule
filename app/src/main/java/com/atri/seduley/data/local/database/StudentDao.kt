package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    fun observeStudentByStudentId(studentId: Long): Flow<StudentEntity?>

    fun getSemesterByStudentId(studentId: Long): Flow<SemesterEntity?> {
        return observeStudentByStudentId(studentId).map { it?.semester }
    }

    @Query("""
        UPDATE students
            SET startDate = :startDate,
            endDate = :endDate,
            totalWeeks = :totalWeeks
        WHERE studentId = :studentId
        """
    )
    suspend fun updateSemester(
        studentId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        totalWeeks: Int
    )

    @Query("SELECT studentId FROM students")
    suspend fun getStudentIds(): List<Long>

    @Query("SELECT studentId FROM students")
    fun observeStudentIds(): Flow<List<Long>>
}