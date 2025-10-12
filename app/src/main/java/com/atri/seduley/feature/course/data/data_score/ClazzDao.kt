package com.atri.seduley.feature.course.data.data_score

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import kotlinx.coroutines.flow.Flow

@Dao
interface ClazzDao {

    @Query("""
        SELECT * FROM clazz
        WHERE weekly = :weekly
        AND :dayOfWeek = NULL OR dayOfWeek = :dayOfWeek
    """)
    fun getClazzByWeeklyAndDayOfWeek(weekly: Int, dayOfWeek: Int? = null): Flow<List<Clazz>>

    /**
     * 若传入 date 与 section 唯一键相同则更新
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClazzes(clazzes: List<Clazz>)

    @Query("""
       SELECT * FROM clazz 
        WHERE date = :date 
    """)
    fun getClazzByDate(date: Long): Flow<List<Clazz>>

    @Query("""
        SELECT COUNT(*) FROM clazz
        WHERE date = :date
    """)
    suspend fun getClazzCountByDate(date: Long):Int

    /**
     * 删除所有 clazz, 危险操作, 谨慎使用
     */
    @Query("""
        DELETE FROM clazz
    """)
    suspend fun deleteAllClazzes()
}