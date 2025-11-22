package com.atri.seduley.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.db.entity.Course
import com.atri.seduley.data.local.db.entity.StudentEntity
import kotlinx.serialization.json.Json

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: String): List<StudentEntity>

    @Query("UPDATE students SET courses = '' WHERE studentId = :studentId")
    suspend fun clearCoursesByStudentId(studentId: String)

    @Query("UPDATE students SET courses = :courses WHERE studentId = :studentId")
    suspend fun updateCourses(studentId: String, courses: String)

    suspend fun updateCourses(studentId: String, courses: List<Course>) {
        updateCourses(studentId, Json.encodeToString(courses))
    }
}