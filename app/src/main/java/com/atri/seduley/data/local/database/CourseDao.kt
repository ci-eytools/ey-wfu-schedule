package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.CourseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("SELECT * FROM courses WHERE studentId = :studentId AND date = :date ORDER BY section ASC")
    fun observeCoursesByStudentIdAndDate(studentId: Long, date: LocalDate): Flow<List<CourseEntity>>

    @Query("DELETE FROM courses WHERE studentId = :studentId")
    suspend fun clearCoursesByStudentId(studentId: Long)

    @Query("DELETE FROM courses")
    suspend fun clearAllCourses()
}