package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.data.source.remote.model.TrainingDto
import com.dncc.dncc.data.source.remote.model.toTrainingEntity
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.utils.checkFirebaseError
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TrainingRepositoryImpl @Inject constructor() : TrainingRepository {
    private val dbTrainingParticipants = Firebase.firestore.collection("trainingParticipants")
    private val dbTraining = Firebase.firestore.collection("trainings")
    private val storageRef: StorageReference =
        FirebaseStorage.getInstance().reference

    override suspend fun addTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val randomId = UUID.randomUUID().toString()
            val data = hashMapOf(
                "trainingId" to randomId,
                "linkWa" to trainingEntity.linkWa,
                "mentor" to trainingEntity.mentor,
                "schedule" to trainingEntity.schedule,
                "trainingName" to trainingEntity.trainingName
            )
            val snapshotListener = dbTraining.document(randomId).set(data).addOnSuccessListener {
                trySend(Resource.Success(true)).isSuccess
                Log.i("TrainingRepositoryImpl", "addTraining with id $randomId success")
            }.addOnFailureListener { error ->
                trySend(Resource.Error(error.checkFirebaseError()))
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun editTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val data = mapOf(
                "linkWa" to trainingEntity.linkWa,
                "mentor" to trainingEntity.mentor,
                "schedule" to trainingEntity.schedule,
                "trainingName" to trainingEntity.trainingName
            )
            val snapshotListener =
                dbTraining.document(trainingEntity.trainingId).update(data).addOnSuccessListener {
                    trySend(Resource.Success(true)).isSuccess
                    Log.i(
                        "TrainingRepositoryImpl",
                        "editTraining with id ${trainingEntity.trainingId} success"
                    )
                }.addOnFailureListener { error ->
                    trySend(Resource.Error(error.checkFirebaseError()))
                }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun getTrainings(): Flow<Resource<List<TrainingEntity>>> =
        callbackFlow {
            val snapshotListener = dbTraining
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val dataUsers = mutableListOf<TrainingDto>()
                        snapshot.forEach {
                            val field = it.toObject(TrainingDto::class.java)
                            dataUsers.add(field)
                        }
                        Resource.Success(dataUsers.map { it.toTrainingEntity() })
                    } else {
                        Resource.Error(e.checkFirebaseError())
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun getTraining(trainingId: String): Flow<Resource<TrainingEntity>> =
        callbackFlow {
            val snapshotListener = dbTraining.document(trainingId)
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val dataUser = snapshot.toObject(TrainingDto::class.java)
                        Resource.Success(dataUser!!.toTrainingEntity())
                    } else {
                        Resource.Error(e.checkFirebaseError())
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun deleteTraining(trainingId: String): Flow<Resource<Boolean>> =
        callbackFlow {
            val snapshotListener =
                dbTraining.document(trainingId).delete()
                    .addOnSuccessListener {
                        trySend(Resource.Success(true)).isSuccess
                        Log.i(
                            "TrainingRepositoryImpl",
                            "deleteTraining with id $trainingId success"
                        )
                    }.addOnFailureListener { error ->
                        trySend(Resource.Error(error.checkFirebaseError()))
                    }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun addMeets(trainingId: String): Flow<Resource<Boolean>> = callbackFlow {
        for (i in 0..9) {
            val randomId = UUID.randomUUID().toString()
            val data = hashMapOf(
                "description" to "deskripsi pelatihan",
                "fileUrl" to "",
                "meetId" to randomId,
                "meetName" to "Pertemuan ${i + 1}"
            )
            val snapshotListener =
                dbTraining.document(trainingId).collection("meets").document(randomId).set(data)
                    .addOnSuccessListener {
                        trySend(Resource.Success(true)).isSuccess
                        Log.i(
                            "TrainingRepositoryImpl",
                            "addMeet with name Pertemuan ${i + 1} success"
                        )
                    }.addOnFailureListener { error ->
                        trySend(Resource.Error(error.checkFirebaseError()))
                    }
            awaitClose { snapshotListener.isCanceled() }
        }
    }

    override suspend fun getMeets(trainingId: String): Flow<Resource<List<MeetEntity>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMeet(meetId: String): Flow<Resource<MeetEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun editMeet(meetEntity: MeetEntity): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFile(
        filePath: String,
        trainingName: String
    ): Flow<Resource<Boolean>> = callbackFlow {
        val file = Uri.fromFile(File(filePath))
        val childStorageRef = storageRef.child(trainingName).child("asdasd")
        val uploadTask = childStorageRef.putFile(file)
        val snapshotListener = uploadTask.addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i("TrainingRepositoryImpl", "uploadImage: success")
                trySend(Resource.Success(true)).isSuccess
            } else {
                trySend(Resource.Error(it.exception.checkFirebaseError())).isSuccess
            }
        }
        awaitClose { snapshotListener.isCanceled() }
    }

}