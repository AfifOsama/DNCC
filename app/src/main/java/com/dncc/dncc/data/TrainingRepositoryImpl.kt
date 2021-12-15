package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.data.source.remote.model.*
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
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
    private val db = Firebase.firestore
    private val dbTraining = Firebase.firestore.collection("trainings")
    private val storageRef: StorageReference =
        FirebaseStorage.getInstance().reference

    override suspend fun addTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val randomId = UUID.randomUUID().toString()
            val snapshotListener = db.runBatch {
                //add training
                val data = hashMapOf(
                    "trainingId" to randomId,
                    "desc" to trainingEntity.desc,
                    "linkWa" to trainingEntity.linkWa,
                    "mentor" to trainingEntity.mentor,
                    "schedule" to trainingEntity.schedule,
                    "trainingName" to trainingEntity.trainingName,
                    "participantMax" to trainingEntity.participantMax,
                    "participantNow" to 0
                )
                dbTraining.document(randomId).set(data)

                //add meets inside training
                for (i in 0..9) {
                    Log.i(
                        "TrainingRepositoryImpl",
                        "addMeet with pertemuan ${i + 1}"
                    )
                    val randomMeetId = UUID.randomUUID().toString()
                    val meet = hashMapOf(
                        "description" to "deskripsi pelatihan",
                        "fileUrl" to "",
                        "meetId" to randomMeetId,
                        "meetName" to "Pertemuan ${i + 1}"
                    )
                    dbTraining.document(randomId).collection("meets").document(randomMeetId)
                        .set(meet)
                }
            }.addOnSuccessListener {
                trySend(Resource.Success(true)).isSuccess
                Log.i(
                    "TrainingRepositoryImpl",
                    "addTraining with trainingId $randomId success"
                )
            }.addOnFailureListener { error ->
                Log.i(
                    "TrainingRepositoryImpl",
                    "addTraining with trainingId $randomId failed"
                )
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
                "trainingName" to trainingEntity.trainingName,
                "participantMax" to trainingEntity.participantMax,
                "desc" to trainingEntity.desc
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
                            Log.i(
                                "TrainingRepositoryImpl",
                                "getTraining with id ${field.trainingId} success"
                            )
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
            val snapshotListener = db.runBatch {
                //delete training
                dbTraining.document(trainingId).delete()

                //delete meets inside training
                dbTraining.document(trainingId).collection("meets").get().addOnCompleteListener {
                    for (document in it.result?.documents!!) {
                        Log.i(
                            "TrainingRepositoryImpl",
                            "delete meet success"
                        )
                        document.reference.delete()
                    }
                }
            }.addOnSuccessListener {
                trySend(Resource.Success(true)).isSuccess
                Log.i(
                    "TrainingRepositoryImpl",
                    "deleteTraining with id $trainingId success"
                )
            }.addOnFailureListener { error ->
                Log.i(
                    "TrainingRepositoryImpl",
                    "deleteTraining with id $trainingId failed"
                )
                trySend(Resource.Error(error.checkFirebaseError()))
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun getParticipants(trainingId: String): Flow<Resource<List<UserEntity>>> =
        callbackFlow {
            val snapshotListener = dbTraining.document(trainingId).collection("users")
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val dataUsers = mutableListOf<UserDto>()
                        snapshot.forEach {
                            val field = it.toObject(UserDto::class.java)
                            dataUsers.add(field)
                            Log.i(
                                "TrainingRepositoryImpl",
                                "getParticipants with id ${field.userId} success"
                            )
                        }
                        Resource.Success(dataUsers.map { it.toUserEntity() })
                    } else {
                        Resource.Error(e.checkFirebaseError())
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun getMeets(trainingId: String): Flow<Resource<List<MeetEntity>>> =
        callbackFlow {
            val snapshotListener = dbTraining.document(trainingId).collection("meets")
                .addSnapshotListener { snapshot, e ->
                    val response = if (snapshot != null) {
                        val dataUsers = mutableListOf<MeetDto>()
                        snapshot.forEach {
                            val field = it.toObject(MeetDto::class.java)
                            dataUsers.add(field)
                            Log.i(
                                "TrainingRepositoryImpl",
                                "getMeets with id ${field.meetId} success"
                            )
                        }
                        Resource.Success(dataUsers.map { it.toMeetEntity() })
                    } else {
                        Resource.Error(e.checkFirebaseError())
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun getMeet(trainingId: String, meetId: String): Flow<Resource<MeetEntity>> =
        callbackFlow {
            val snapshotListener =
                dbTraining.document(trainingId).collection("meets").document(meetId)
                    .addSnapshotListener { snapshot, e ->
                        val response = if (snapshot != null) {
                            val dataUser = snapshot.toObject(MeetDto::class.java)
                            Resource.Success(dataUser!!.toMeetEntity())
                        } else {
                            Resource.Error(e.checkFirebaseError())
                        }
                        trySend(response).isSuccess
                    }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun editMeet(meetEntity: MeetEntity): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    suspend fun uploadFile(
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