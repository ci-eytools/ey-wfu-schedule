package com.atri.seduley.feature.setting.domain.entity

/**
 * 用户凭证
 */
data class UserCredential(

    /** 学号 */
    val studentId: String?,

    /** 密码 */
    val password: String?
)