package com.atri.seduley.feature.course.data.data_score

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.feature.course.domain.entity.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("""
        SELECT * FROM course 
        WHERE id = :id
    """)
    suspend fun getCourseById(id: Long): Course

    /**
     * 获取所有 course
     */
    @Query("""
        SELECT * FROM course
    """)
    fun getAllCourses(): Flow<List<Course>>

    /**
     * 获取所有 course
     */
    @Query("""
        SELECT * FROM course
    """)
    suspend fun getAllCoursesOnce(): List<Course>

    /**
     * 查询指定周的所有 course
     *
     * @param week 由 `0b10` 开始计数
     */
    @Query("""
        SELECT * FROM course
        WHERE (weeks & (1 << :week)) != 0
    """)
    fun getCoursesByWeekly(week: Int): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    /**
     * 删除所有 course, 危险操作, 谨慎使用
     */
    @Query("""
        DELETE FROM course
    """)
    suspend fun deleteAllCourses()
}