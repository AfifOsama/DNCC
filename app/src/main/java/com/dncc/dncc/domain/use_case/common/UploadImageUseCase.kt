package com.dncc.dncc.domain.use_case.common

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(path: String, userId: String): Flow<Resource<Boolean>> =
        userRepository.uploadImage(path, userId)
}