package com.atri.seduley.feature.course.domain.repository

import com.atri.seduley.feature.course.domain.entity.model.Clazz
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface ClazzRepository {

    /**
     * 根据周次和星期查找上课信息
     */
    fun getClazzByWeeklyAndDayOfWeek(
        weekly: Int, dayOfWeek: Int? = null
    ): Flow<List<Clazz>>

    /**
     * 根据日期查找上课信息条数
     */
    suspend fun getClazzCountByDate(date: LocalDate): Int

    /**
     * 根据日期查找上课信息
     */
    fun getClazzByDate(date: LocalDate): Flow<List<Clazz>>

    /**
     * 插入 clazzes
     */
    suspend fun insertClazzes(clazzes: List<Clazz>)

    /**
     * 删除所有 clazz
     */
    suspend fun deleteAllClazzes()
}