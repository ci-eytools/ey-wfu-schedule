package com.atri.seduley.feature.course.domain.repository

import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import com.atri.seduley.feature.course.domain.entity.dto.ParsedCourse
import com.atri.seduley.feature.course.domain.entity.model.Clazz
import com.atri.seduley.feature.course.domain.entity.model.Course
import org.threeten.bp.LocalDate

interface InitInfoRepository {

    /**
     * 发起连接
     */
    suspend fun connection(
        username: String,
        password: String,
        captcha: String? = null
    )

    /**
     * 拉取所有初始信息
     */
    suspend fun enterOverallInfo(
        username: String,
        password: String,
        captcha: String? = null
    ): List<ParsedCourse>

    /**
     * 获取指定日期的信息
     */
    suspend fun enterInfo(
        username: String,
        password: String,
        date: LocalDate,
        captcha: String? = null
    ): List<ParsedCourse>

    /**
     * 获取基础信息
     */
    suspend fun getBaseInfo(
        username: String,
        password: String,
        captcha: String? = null
    ): BaseInfoDTO

    /**
     * 部分更新(根据唯一键, course 忽略, clazz 覆盖)
     */
    suspend fun insertInfo(courses: List<Course>, clazzes: List<Clazz>)

    /**
     * 全量更新(清空数据库再添加数据)
     */
    suspend fun insertOverallInfo(courses: List<Course>, clazzes: List<Clazz>, enterMark: Int)
}