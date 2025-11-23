package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity
import kotlinx.serialization.json.Json

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: String): StudentEntity?

    @Query("SELECT semester FROM students WHERE studentId = :studentId")
    suspend fun getSemesterByStudentId(studentId: String): SemesterEntity?

    @Query("SELECT courses FROM students WHERE studentId = :studentId")
    suspend fun getCoursesByStudentId(studentId: String): List<CourseEntity>

    @Query("UPDATE students SET courses = '' WHERE studentId = :studentId")
    suspend fun clearCoursesByStudentId(studentId: String)

    @Query("UPDATE students SET courses = ''")
    suspend fun clearAllCourses()

    @Query("UPDATE students SET courses = :courses WHERE studentId = :studentId")
    suspend fun updateCourses(studentId: String, courses: String)

    @Query("UPDATE students SET semester = :semester WHERE studentId = :studentId")
    suspend fun updateSemester(studentId: String, semester: String)

    suspend fun updateCourses(studentId: String, courses: List<CourseEntity>) {
        updateCourses(studentId, Json.encodeToString(courses))
    }

    suspend fun updateSemester(studentId: String, semester: SemesterEntity) {
        updateSemester(studentId, Json.encodeToString(semester))
    }
}