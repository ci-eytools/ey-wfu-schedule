package com.atri.seduley.feature.course.domain.entity.dto

class BaseInfoDTO(
    var college: String = "",                     // 学院
    var major: String = "",                       // 专业
    var startDate: Long = Long.MIN_VALUE,         // 开课时间(时间戳)
    var endDate: Long = Long.MAX_VALUE,           // 结课时间
    var enterMark: Int = 0
)