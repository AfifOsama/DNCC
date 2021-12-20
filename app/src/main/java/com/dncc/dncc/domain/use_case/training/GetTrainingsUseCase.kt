package com.dncc.dncc.domain.use_case.training

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.entity.training.TrainingEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrainingsUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<TrainingEntity>>> =
        trainingRepository.getTrainings()
}