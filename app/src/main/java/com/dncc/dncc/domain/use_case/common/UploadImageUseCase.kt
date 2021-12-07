package com.dncc.dncc.domain.use_case.common

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase@Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(path: String, userId: String): Flow<Resource<Boolean>> =
        mainRepository.uploadImage(path, userId)
}