package com.dncc.dncc.domain.use_case

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<Resource<Boolean>> =
        mainRepository.login(email, password)
}