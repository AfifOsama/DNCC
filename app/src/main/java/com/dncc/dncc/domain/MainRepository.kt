package com.dncc.dncc.domain

import com.dncc.dncc.common.Resource
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun login(): Flow<Resource<String>>
}