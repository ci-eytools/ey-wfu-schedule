package com.atri.seduley.feature.course.domain.entity.model

data class BaseInfo(
    val id: Long,
    val college: String,                     // 学院
    val major: String,                       // 专业
    val startDate: Long,                     // 开课时间, 周一(时间戳)
    val endDate: Long,                       // 结课时间, 周日(时间戳)
    val enterMark: Long                      // 周课表拉取标志位(1: 已拉取数据, 0: 未拉取数据)
)