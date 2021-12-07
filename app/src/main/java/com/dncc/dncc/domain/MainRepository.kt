package com.dncc.dncc.domain

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
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
    suspend fun getUser(userId: String): Flow<Resource<UserEntity>>
    suspend fun getUsers(): Flow<Resource<List<UserEntity>>>
    suspend fun editUser(userEntity: UserEntity): Flow<Resource<Boolean>>
    suspend fun addTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun editTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun getTrainings(trainingEntity: TrainingEntity): Flow<Resource<List<TrainingEntity>>>
    suspend fun getTraining(trainingEntity: TrainingEntity): Flow<Resource<TrainingEntity>>
    suspend fun registerTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun getMeets(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun getMeet(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun editMeet(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun uploadFile(filePath: String, trainingName: String): Flow<Resource<Boolean>>
    suspend fun logout(): Flow<Resource<Boolean>>
}