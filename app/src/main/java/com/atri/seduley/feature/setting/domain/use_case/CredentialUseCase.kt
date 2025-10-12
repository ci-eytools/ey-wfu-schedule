package com.atri.seduley.feature.setting.domain.use_case

import com.atri.seduley.core.exception.CredentialException
import com.atri.seduley.feature.course.domain.entity.dto.BaseInfoDTO
import com.atri.seduley.feature.course.domain.repository.BaseInfoRepository
import com.atri.seduley.feature.course.domain.repository.InitInfoRepository
import com.atri.seduley.feature.setting.domain.entity.UserCredential
import com.atri.seduley.feature.setting.domain.repository.UserCredentialRepository
import javax.inject.Inject

data class CredentialUseCases @Inject constructor(
    val saveCredential: SaveCredential,
    val getStudentId: GetStudentId,
    val clearCredential: ClearCredential
)

class SaveCredential @Inject constructor(
    private val userCredentialRepository: UserCredentialRepository,
    private val initInfoRepository: InitInfoRepository,
    private val baseInfoRepository: BaseInfoRepository
) {
    suspend operator fun invoke(credential: UserCredential) {
        if (credential.studentId.isNullOrEmpty() || credential.password.isNullOrEmpty())
            throw CredentialException()
        // 此处进行检查, 如果发生异常则由上层调度进行处理
        initInfoRepository.connection(credential.studentId, credential.password)

        userCredentialRepository.saveCredential(credential)

        // 保存基础信息
        val baseInfoDTO = initInfoRepository
            .getBaseInfo(credential.studentId, credential.password)
        baseInfoRepository.saveBaseInfo(
            BaseInfoDTO(
                startDate = baseInfoDTO.startDate,
                endDate = baseInfoDTO.endDate,
                college = baseInfoDTO.college,
                major = baseInfoDTO.major
            )
        )
    }
}

class GetStudentId @Inject constructor(
    private val repository: UserCredentialRepository
) {
    suspend operator fun invoke(): String = repository.getStudentId()
}

class ClearCredential @Inject constructor(
    private val repository: UserCredentialRepository
) {
    suspend operator fun invoke() = repository.clearCredential()
}