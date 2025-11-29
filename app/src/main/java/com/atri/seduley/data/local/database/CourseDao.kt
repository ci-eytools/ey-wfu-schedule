package com.atri.seduley.data.local.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.CourseEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Query("SELECT * FROM course WHERE studentId = :studentId AND date = :date ORDER BY section ASC")
    fun getCoursesByStudentIdAndDate(studentId: Long, date: LocalDate): Flow<List<CourseEntity>>

    @Query("DELETE FROM course WHERE studentId = :studentId")
    suspend fun clearCoursesByStudentId(studentId: Long)

    @Query("DELETE FROM course")
    suspend fun clearAllCourses()
}