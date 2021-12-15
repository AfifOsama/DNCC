package com.dncc.dncc.domain.use_case.training

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetParticipantsUseCase @Inject constructor(
    private val trainingRepository: TrainingRepository
) {
    suspend operator fun invoke(trainingId: String): Flow<Resource<List<UserEntity>>> =
        trainingRepository.getParticipants(trainingId)
}