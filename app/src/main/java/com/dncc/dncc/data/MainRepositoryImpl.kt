package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.TrainingEnum
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
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
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainRepositoryImpl @Inject constructor() : MainRepository {
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val dbTrainingParticipants = Firebase.firestore.collection("trainingParticipants")
    private val dbTraining = Firebase.firestore.collection("trainings")
    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

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
                            ).isFailure
                        }
                    }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun uploadImage(path: String, userId: String): Flow<Resource<String>> =
        callbackFlow {
            val file = Uri.fromFile(File(path))
            val storageRef = imagesRef.child(userId)
            val uploadTask = storageRef.putFile(file)
            val snapshotListener = uploadTask.addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("MainRepositoryImpl", "uploadImage: success")
                    trySend(Resource.Success("Success")).isSuccess
                } else {
                    trySend(
                        Resource.Error(
                            it.exception?.message ?: "Gagal mendaftarkan akun"
                        )
                    ).isFailure
                }
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun registerFirestore(
        registerEntity: RegisterEntity,
        userId: String
    ): Flow<Resource<String>> = callbackFlow {
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
            trySend(Resource.Success("Success")).isSuccess
            Log.i("MainRepositoryImpl", "registerToFirestore user success")
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun login(email: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {

//            val snapshotListener = auth.createUserWithEmailAndPassword(registerEntity.email, registerEntity.password)
//                .addOnCompleteListener {
//                    val response = if (it.isSuccessful) {
//                        Log.i("MainRepositoryImpl", "create user success")
//                        val userId = auth.currentUser?.uid ?: ""
//                        Log.i("MainRepositoryImpl", "userId: $userId")
//                        if (uploadImage(registerEntity.photoPath, userId)) {
//                            Log.i("MainRepositoryImpl", "upload user image success")
//                            registerToFirestore(registerEntity, userId)
//                        } else {
//                            false
//                        }
//                    } else {
//                        false
//                    }
//                    if (response) {
//                        trySend(Resource.Success(true)).isSuccess
//                    } else {
//                        trySend(Resource.Error(it.exception?.message ?: "Gagal mendaftarkan akun")).isFailure
//                    }
//                }
//            awaitClose { snapshotListener.isCanceled() }
//
        }

}