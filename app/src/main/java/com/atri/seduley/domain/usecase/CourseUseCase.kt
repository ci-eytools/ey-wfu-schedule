package com.atri.seduley.domain.usecase

import com.atri.seduley.data.local.database.StudentDao
import com.atri.seduley.data.remote.api.CourseApi
import javax.inject.Inject

data class CourseUseCase @Inject constructor(
    private val studentDao: StudentDao,
    private val courseApi: CourseApi
) {


}