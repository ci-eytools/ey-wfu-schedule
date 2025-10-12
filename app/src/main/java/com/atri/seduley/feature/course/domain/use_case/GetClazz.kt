package com.atri.seduley.feature.course.domain.use_case

import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.repository.ClazzRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetClazz @Inject constructor(
    private val repository: ClazzRepository
) {

    operator fun invoke(weekly: Int, dayOfWeek: Int? = null): Flow<List<Clazz>> =
        repository.getClazzByWeeklyAndDayOfWeek(weekly, dayOfWeek)
}