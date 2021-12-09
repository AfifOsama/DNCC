package com.dncc.dncc.domain.use_case.user

import androidx.lifecycle.MutableLiveData
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.UserRepository
import com.dncc.dncc.domain.entity.user.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Flow<Resource<UserEntity>> =
        userRepository.getUser(userId)

    val observeUser: MutableLiveData<Resource<UserEntity>> = userRepository.getUserResponse
}