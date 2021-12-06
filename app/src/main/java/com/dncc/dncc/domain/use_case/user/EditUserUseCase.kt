package com.dncc.dncc.domain.use_case.user

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditUserUseCase @Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(userEntity: UserEntity): Flow<Resource<Boolean>> =
        mainRepository.editUser(userEntity)
}