package com.dncc.dncc.domain.use_case.user

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.UserRepository
import com.dncc.dncc.domain.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<UserEntity>>> = userRepository.getUsers()
}