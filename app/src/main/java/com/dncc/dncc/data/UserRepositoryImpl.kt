package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.TrainingEnum
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.data.source.remote.model.UserDto
import com.dncc.dncc.data.source.remote.model.toUserEntity
import com.dncc.dncc.domain.UserRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.utils.checkFirebaseError
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
class UserRepositoryImpl @Inject constructor() : UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    override suspend fun register(registerEntity: RegisterEntity): Flow<Resource<String>> =
        callbackFlow {
            val snapshotListener =
                auth.createUserWithEmailAndPassword(registerEntity.email, registerEntity.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.i("UserRepositoryImpl", "create user success")
                            val userId = auth.currentUser?.uid ?: ""
                            trySend(Resource.Success(userId)).isSuccess
                        } else {
                            trySend(Resource.Error(it.exception.checkFirebaseError())).isSuccess
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
                    Log.i("UserRepositoryImpl", "uploadImage: success")
                    trySend(Resource.Success(true)).isSuccess
                } else {
                    trySend(Resource.Error(it.exception.checkFirebaseError())).isSuccess
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
                "training" to TrainingEnum.EMPTY.trainingName,
                "trainingId" to ""
            )
        ).addOnSuccessListener {
            trySend(Resource.Success(true)).isSuccess
            Log.i("UserRepositoryImpl", "registerToFirestore user success")
        }.addOnFailureListener { error ->
            trySend(Resource.Error(error.checkFirebaseError()))
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun login(email: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {
            Log.i("UserRepositoryImpl", "login")
            val snapshotListener = auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.i("UserRepositoryImpl", "sign in success")
                        trySend(Resource.Success(true)).isSuccess
                    } else {
                        Log.i("UserRepositoryImpl", "login failed")
                        trySend(Resource.Error(it.exception.checkFirebaseError())).isSuccess
                    }
                }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun passwordReset(email: String): Flow<Resource<Boolean>> = callbackFlow {
        val snapshotListener = auth.sendPasswordResetEmail(email)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("UserRepositoryImpl", "send email forgot passowrd success")
                    trySend(Resource.Success(true)).isSuccess
                } else {
                    trySend(Resource.Error(it.exception.checkFirebaseError())).isSuccess
                }
            }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun getUser(userId: String): Flow<Resource<UserEntity>> = callbackFlow {
        val snapshotListener = dbUsers.document(userId)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val dataUser = snapshot.toObject(UserDto::class.java)
                    Resource.Success(dataUser?.toUserEntity() ?: UserEntity())
                } else {
                    Resource.Error(e.checkFirebaseError())
                }
                trySend(response).isSuccess
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun getUsers(): Flow<Resource<List<UserEntity>>> = callbackFlow {
        val snapshotListener = dbUsers
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val dataUsers = mutableListOf<UserDto>()
                    snapshot.forEach {
                        val field = it.toObject(UserDto::class.java)
                        dataUsers.add(field)
                    }
                    Resource.Success(dataUsers.map { it.toUserEntity() })
                } else {
                    Resource.Error(e.checkFirebaseError())
                }
                trySend(response).isSuccess
            }
        awaitClose { snapshotListener.remove() }
    }

    override suspend fun editUser(userEntity: UserEntity): Flow<Resource<Boolean>> = callbackFlow {
        Log.i("UserRepositoryImpl", "editUser ${userEntity.userId}")
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
            Log.i("UserRepositoryImpl", "editUser ${userEntity.fullName} success")
        }.addOnFailureListener { error ->
            trySend(Resource.Error(error.checkFirebaseError()))
        }
        awaitClose { snapshotListener.isCanceled() }
    }

    override suspend fun registerTraining(trainingId: String): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Flow<Resource<Boolean>> = callbackFlow {
        auth.signOut()
        trySend(Resource.Success(true)).isSuccess
    }
}