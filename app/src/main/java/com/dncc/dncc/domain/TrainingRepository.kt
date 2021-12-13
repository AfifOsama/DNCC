package com.dncc.dncc.domain

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import kotlinx.coroutines.flow.Flow

interface TrainingRepository {
    suspend fun addTraining(trainingEntity: TrainingEntity): Flow<Resource<String>>
    suspend fun editTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>>
    suspend fun getTrainings(): Flow<Resource<List<TrainingEntity>>>
    suspend fun getTraining(trainingId: String): Flow<Resource<TrainingEntity>>
    suspend fun deleteTraining(trainingId: String): Flow<Resource<Boolean>>
    suspend fun addMeets(trainingId: String): Flow<Resource<Boolean>>
    suspend fun getMeets(trainingId: String): Flow<Resource<List<MeetEntity>>>
    suspend fun getMeet(meetId: String): Flow<Resource<MeetEntity>>
    suspend fun editMeet(meetEntity: MeetEntity): Flow<Resource<Boolean>>
    suspend fun uploadFile(filePath: String, trainingName: String): Flow<Resource<Boolean>>
}