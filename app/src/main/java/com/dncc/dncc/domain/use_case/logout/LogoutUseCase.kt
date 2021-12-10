package com.dncc.dncc.domain.use_case.logout

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<Boolean>> = userRepository.logout()
}