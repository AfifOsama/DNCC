package com.dncc.dncc.domain.use_case

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(registerEntity: RegisterEntity): Flow<Resource<String>> =
        mainRepository.register(registerEntity)
}