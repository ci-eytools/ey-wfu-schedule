package com.atri.seduley.domain.result

import com.atri.seduley.domain.model.Course

sealed interface CourseResult {
    data class Success(val courses: List<Course>) : CourseResult
    data class AuthError(val msg: String) : CourseResult
    data object UnknownError : CourseResult
}