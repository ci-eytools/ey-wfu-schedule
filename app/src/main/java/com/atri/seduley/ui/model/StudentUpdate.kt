package com.atri.seduley.ui.model

data class StudentUpdate(
    val studentId: String,
    val password: String,
    val nickname: String? = null
)