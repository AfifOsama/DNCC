package com.dncc.dncc.domain.use_case.user

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(userId: String): Flow<Resource<UserEntity>> =
        mainRepository.getUser(userId)
}