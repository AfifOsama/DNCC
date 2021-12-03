package com.dncc.dncc.domain.use_case

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UploadImageUseCase@Inject constructor(
    private val mainRepository: MainRepository
) {
    suspend operator fun invoke(path: String, userId: String): Flow<Resource<String>> =
        mainRepository.uploadImage(path, userId)
}