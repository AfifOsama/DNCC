package com.dncc.dncc.domain.use_case.reset_password

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PasswordResetUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String): Flow<Resource<Boolean>> =
        userRepository.passwordReset(email)
}