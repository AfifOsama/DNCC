package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.TrainingEnum
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.data.source.remote.model.TrainingDto
import com.dncc.dncc.data.source.remote.model.UserDto
import com.dncc.dncc.data.source.remote.model.toTrainingEntity
import com.dncc.dncc.data.source.remote.model.toUserEntity
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.util.UUID.randomUUID
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainRepositoryImpl @Inject constructor() : MainRepository {
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val dbTrainingParticipants = Firebase.firestore.collection("trainingParticipants")
    private val dbTraining = Firebase.firestore.collection("trainings")
    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")
    private val storageRef: StorageReference =
        FirebaseStorage.getInstance().reference

    override suspend fun register(registerEntity: RegisterEntity): Flow<Resource<String>> =
        callbackFlow {
            val snapshotListener =
                auth.createUserWithEmailAndPassword(registerEntity.email, registerEntity.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.i("MainRepositoryImpl", "create user success")
                            val userId = auth.currentUser?.uid ?: ""
                            trySend(Resource.Success(userId)).isSuccess
                        } else {
                            trySend(
                                Resource.Error(
                                    it.exception?.message ?: "Gagal mendaftarkan akun"
                                )
                            ).isSuccess
                        }
                    }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun uploadImage(path: String, userId: String): Flow<Resource<Boolean>> =
        callbackFlow {
            val file = Uri.fromFile(File(path))
            val storageRef = imagesRef.child(userId)
            val uploadTask = storageRef.putFile(file)
            val snapshotListener = uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("MainRepositoryImpl", "uploadImage: success")
                    trySend(Resource.Success(true)).isSuccess
                } else {
                    trySend(
                        Resource.Error(
                            it.exception?.message ?: "Gagal upload gambar"
                        )
                    ).isSuccess
                }
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun registerFirestore(
        registerEntity: RegisterEntity,
        userId: String
    ): Flow<Resource<Boolean>> = callbackFlow {
        val snapshotListener = dbUsers.document(userId).set(
            mapOf(
                "email" to registerEntity.email,
                "fullName" to registerEntity.fullName,
                "major" to registerEntity.major,
                "nim" to registerEntity.nim,
                "noHp" to registerEntity.noHp,
                "role" to UserRoleEnum.MEMBER.role,
                "userId" to userId,
                "training" to TrainingEnum.EMPTY.trainingName
            )
        ).addOnSuccessListener {
            trySend(Resource.Success(true)).isSuccess
            Log.i("MainRepositoryImpl", "registerToFirestore user success")
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun login(email: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {
            Log.i("MainRepositoryImpl", "login")
            val snapshotListener = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i("MainRepositoryImpl", "sign in success")
                        trySend(Resource.Success(true)).isSuccess
                    } else {
                        Log.i("MainRepositoryImpl", "login failed")
                        trySend(
                            Resource.Error(
                                it.exception?.message ?: "Gagal login"
                            )
                        ).isSuccess
                    }
                }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun passwordReset(email: String): Flow<Resource<Boolean>> = callbackFlow {
        val snapshotListener = auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("MainRepositoryImpl", "send email forgot passowrd success")
                    trySend(Resource.Success(true)).isSuccess
                } else {
                    trySend(
                        Resource.Error(
                            it.exception?.message ?: "Gagal mendaftarkan akun"
                        )
                    ).isSuccess
                }
            }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun getUser(userId: String): Flow<Resource<UserEntity>> = callbackFlow {
        val snapshotListener = dbUsers.document(userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) throw e
                val response = if (snapshot != null) {
                    val dataUser = snapshot.toObject(UserDto::class.java)
                    Resource.Success(dataUser!!.toUserEntity())
                } else {
                    Resource.Error("")
                }
                trySend(response).isSuccess
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getUsers(): Flow<Resource<List<UserEntity>>> = callbackFlow {
        val snapshotListener = dbUsers
            .addSnapshotListener { snapshot, e ->
                if (e != null) throw e
                val response = if (snapshot != null) {
                    val dataUsers = mutableListOf<UserDto>()
                    snapshot.forEach {
                        val field = it.toObject(UserDto::class.java)
                        dataUsers.add(field)
                    }
                    Resource.Success(dataUsers.map { it.toUserEntity() })
                } else {
                    Resource.Error("")
                }
                trySend(response).isSuccess
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun editUser(userEntity: UserEntity): Flow<Resource<Boolean>> = callbackFlow {
        Log.i("MainRepositoryImpl", "editUser ${userEntity.userId}")
        val snapshotListener = dbUsers.document(userEntity.userId).update(
            mapOf(
                "fullName" to userEntity.fullName,
                "major" to userEntity.major,
                "nim" to userEntity.nim,
                "noHp" to userEntity.noHp,
                "training" to userEntity.training,
                "role" to userEntity.role
            )
        ).addOnSuccessListener {
            trySend(Resource.Success(true)).isSuccess
            Log.i("MainRepositoryImpl", "editUser ${userEntity.fullName} success")
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun addTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val randomId = randomUUID().toString()
            val data = hashMapOf(
                "trainingId" to randomId,
                "linkWa" to trainingEntity.linkWa,
                "mentor" to trainingEntity.mentor,
                "schedule" to trainingEntity.schedule,
                "trainingName" to trainingEntity.trainingName
            )
            val snapshotListener = dbTraining.document(randomId).set(data).addOnSuccessListener {
                trySend(Resource.Success(true)).isSuccess
                Log.i("MainRepositoryImpl", "addTraining with id $randomId success")
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
                        "MainRepositoryImpl",
                        "editTraining with id ${trainingEntity.trainingId} success"
                    )
                }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun getTrainings(trainingEntity: TrainingEntity): Flow<Resource<List<TrainingEntity>>> =
        callbackFlow {
            val snapshotListener = dbTraining
                .addSnapshotListener { snapshot, e ->
                    if (e != null) throw e
                    val response = if (snapshot != null) {
                        val dataUsers = mutableListOf<TrainingDto>()
                        snapshot.forEach {
                            val field = it.toObject(TrainingDto::class.java)
                            dataUsers.add(field)
                        }
                        Resource.Success(dataUsers.map { it.toTrainingEntity() })
                    } else {
                        Resource.Error("")
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun getTraining(trainingEntity: TrainingEntity): Flow<Resource<TrainingEntity>> =
        callbackFlow {
            val snapshotListener = dbTraining.document(trainingEntity.trainingId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) throw e
                    val response = if (snapshot != null) {
                        val dataUser = snapshot.toObject(TrainingDto::class.java)
                        Resource.Success(dataUser!!.toTrainingEntity())
                    } else {
                        Resource.Error("")
                    }
                    trySend(response).isSuccess
                }
            awaitClose { snapshotListener.remove() }
        }

    override suspend fun registerTraining(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMeets(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMeet(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun editMeet(trainingEntity: TrainingEntity): Flow<Resource<Boolean>> {
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
                Log.i("MainRepositoryImpl", "uploadImage: success")
                trySend(Resource.Success(true)).isSuccess
            } else {
                trySend(
                    Resource.Error(
                        it.exception?.message ?: "Gagal upload gambar"
                    )
                ).isSuccess
            }
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun logout(): Flow<Resource<Boolean>> = callbackFlow{
        auth.signOut()
        trySend(Resource.Success(true)).isSuccess
    }


}