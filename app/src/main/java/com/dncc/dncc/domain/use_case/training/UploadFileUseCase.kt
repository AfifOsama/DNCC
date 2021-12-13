package com.dncc.dncc.domain.use_case.training

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(filePath: String, trainingName: String): Flow<Resource<Boolean>> =
        trainingRepository.uploadFile(filePath, trainingName)
}