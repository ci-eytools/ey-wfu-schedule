package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * 获取课程信息
 */
class GetClazz @Inject constructor(
    private val repository: ClazzRepository
) {

    operator fun invoke(
        weekly: Int? = null,
        dayOfWeek: Int? = null,
        date: LocalDate? = null
    ): Flow<List<Clazz>> =
        if (weekly != null && dayOfWeek != null) {
            repository.getClazzByWeeklyAndDayOfWeek(weekly, dayOfWeek)
        } else if (date != null) {
            repository.getClazzByDate(date)
        } else {
            emptyFlow()
        }
}