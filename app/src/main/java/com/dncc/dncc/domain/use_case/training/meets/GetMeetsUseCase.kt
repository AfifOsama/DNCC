package com.dncc.dncc.domain.use_case.training.meets

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.entity.training.MeetEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeetsUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(trainingId: String): Flow<Resource<List<MeetEntity>>> =
        trainingRepository.getMeets(trainingId)
}