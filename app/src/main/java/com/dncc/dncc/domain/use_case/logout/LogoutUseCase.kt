package com.dncc.dncc.domain.use_case.logout

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(): Flow<Resource<Boolean>> = mainRepository.logout()
}