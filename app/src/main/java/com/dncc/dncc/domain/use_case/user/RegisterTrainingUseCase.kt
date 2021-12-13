package com.dncc.dncc.domain.use_case.user

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterTrainingUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(trainingId: String): Flow<Resource<Boolean>> =
        userRepository.registerTraining(trainingId)
}