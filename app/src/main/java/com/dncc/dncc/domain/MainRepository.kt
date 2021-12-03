package com.dncc.dncc.domain

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.register.RegisterEntity
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun register(registerEntity: RegisterEntity): Flow<Resource<String>>
    suspend fun uploadImage(path: String, userId: String): Flow<Resource<Boolean>>
    suspend fun registerFirestore(
        registerEntity: RegisterEntity,
        userId: String
    ): Flow<Resource<Boolean>>
    suspend fun login(email: String, password: String): Flow<Resource<Boolean>>
    suspend fun passwordReset(email: String): Flow<Resource<Boolean>>
}