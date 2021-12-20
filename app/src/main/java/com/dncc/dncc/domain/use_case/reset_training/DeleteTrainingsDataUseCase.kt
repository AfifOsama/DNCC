package com.dncc.dncc.domain.use_case.reset_training

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteTrainingsDataUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(): Flow<Resource<Boolean>> = trainingRepository.deleteTrainingsData()
}