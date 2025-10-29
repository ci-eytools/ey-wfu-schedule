package com.atri.seduley.feature.course.data.repository

import com.atri.seduley.core.alarm.util.AppLogger
import com.atri.seduley.core.util.TimeUtil
import com.atri.seduley.feature.course.data.data_score.ClazzDao
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

/**
 * 课程存储库实现
 */
class ClazzRepositoryImpl(
    private val dao: ClazzDao
) : ClazzRepository {

    /**
     * 根据周次和星期查找上课信息
     */
    override fun getClazzByWeeklyAndDayOfWeek(
        weekly: Int, dayOfWeek: Int?
    ): Flow<List<Clazz>> =
        dao.getClazzByWeeklyAndDayOfWeek(weekly, dayOfWeek)

    /**
     * 根据日期查找上课信息条数
     */
    override suspend fun getClazzCountByDate(date: LocalDate): Int =
        dao.getClazzCountByDate(TimeUtil.localDateToTimestamp(date))

    /**
     * 根据日期查找上课信息
     */
    override fun getClazzByDate(date: LocalDate): Flow<List<Clazz>> {
        val localDateToTimestamp = TimeUtil.localDateToTimestamp(date)
        AppLogger.d("localDateToTimestamp: $localDateToTimestamp")
        return dao.getClazzByDate(localDateToTimestamp)
    }

    /**
     * 根据日期查找上课信息 (获取一次)
     */
    override suspend fun getClazzByDateOnce(date: LocalDate): List<Clazz> =
        dao.getClazzByDateOnce(TimeUtil.localDateToTimestamp(date))


    /**
     * 插入 clazzes
     */
    override suspend fun insertClazzes(clazzes: List<Clazz>) {
        dao.insertClazzes(clazzes)
    }

    override suspend fun deleteAllClazzes() = dao.deleteAllClazzes()

}