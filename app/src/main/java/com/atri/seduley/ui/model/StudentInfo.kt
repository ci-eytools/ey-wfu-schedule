package com.atri.seduley.ui.model

import com.atri.seduley.domain.model.Student
import java.time.LocalDateTime

data class StudentInfo(
    val studentId: String,
    val nickName: String,
    val courseUpdatedAt: LocalDateTime,
    val params: Map<String, String>
)

fun Student.toStudentInfo(): StudentInfo {
    return StudentInfo(
        studentId = this.studentId.toString(),
        nickName = this.nickName,
        courseUpdatedAt = this.courseUpdatedAt,
        params = this.params
    )
}