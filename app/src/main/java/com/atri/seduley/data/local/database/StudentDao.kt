package com.atri.seduley.data.local.database

import androidx.room.*
import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.data.local.database.entity.SemesterEntity
import com.atri.seduley.data.local.database.entity.StudentEntity

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getStudentByStudentId(studentId: String): StudentEntity?

    // 使用 TypeConverter 自动解析 SemesterEntity
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getSemesterByStudentIdRaw(studentId: String): StudentEntity?

    suspend fun getSemesterByStudentId(studentId: String): SemesterEntity? {
        return getSemesterByStudentIdRaw(studentId)?.semester
    }

    // 使用 TypeConverter 自动解析 List<CourseEntity>
    @Query("SELECT * FROM students WHERE studentId = :studentId")
    suspend fun getCoursesByStudentIdRaw(studentId: String): StudentEntity?

    suspend fun getCoursesByStudentId(studentId: String): List<CourseEntity> {
        return getCoursesByStudentIdRaw(studentId)?.courses ?: emptyList()
    }

    // 清空课程
    suspend fun clearCoursesByStudentId(studentId: String) {
        val student = getStudentByStudentId(studentId) ?: return
        val updated = student.copy(courses = emptyList())
        updateStudent(updated)
    }

    // 清空所有课程
    @Query("SELECT * FROM students")
    suspend fun getAllStudents(): List<StudentEntity>

    suspend fun clearAllCourses() {
        val allStudents = getAllStudents()
        allStudents.forEach { student ->
            val updated = student.copy(courses = emptyList())
            updateStudent(updated)
        }
    }

    // 更新 courses
    suspend fun updateCourses(studentId: String, courses: List<CourseEntity>) {
        val student = getStudentByStudentId(studentId) ?: return
        val updated = student.copy(courses = courses)
        updateStudent(updated)
    }

    // 更新 semester
    suspend fun updateSemester(studentId: String, semester: SemesterEntity) {
        val student = getStudentByStudentId(studentId) ?: return
        val updated = student.copy(semester = semester)
        updateStudent(updated)
    }

    // 删除学生
    @Query("DELETE FROM students WHERE studentId = :studentId")
    suspend fun clearByStudentId(studentId: String)

    // Room update 方法
    @Update
    suspend fun updateStudent(student: StudentEntity)
}