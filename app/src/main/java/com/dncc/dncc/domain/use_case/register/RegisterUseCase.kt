package com.dncc.dncc.domain.use_case.register

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.UserRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<Resource<String>> =
        userRepository.register(email, password)
}