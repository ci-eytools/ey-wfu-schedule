package com.atri.seduley.domain.model

import com.atri.seduley.data.local.database.entity.SemesterEntity
import java.time.LocalDateTime

data class Student(
    val studentId: String = "",  // 唯一索引
    val semester: SemesterEntity = SemesterEntity(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)