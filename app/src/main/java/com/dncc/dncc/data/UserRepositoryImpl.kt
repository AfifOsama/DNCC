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
import com.dncc.dncc.domain.entity.user.EditUserEntity
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
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = Firebase.firestore.collection("users")
    private val imagesRef: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")
    private val dbTraining = Firebase.firestore.collection("trainings")

    override suspend fun register(registerEntity: RegisterEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val snapshotListener = db.runBatch {
                //create user
                auth.createUserWithEmailAndPassword(registerEntity.email, registerEntity.password)
                    .addOnSuccessListener {
                        Log.i(
                            "UserRepositoryImpl",
                            "createUserWithEmailAndPassword success"
                        )
                        val userId = it.user?.uid ?: ""

                        //upload photo
                        val file = Uri.fromFile(File(registerEntity.photoPath))
                        val storageRef = imagesRef.child(userId)
                        storageRef.putFile(file)

                        //put in firestore
                        dbUsers.document(userId).set(
                            mapOf(
                                "email" to registerEntity.email,
                                "fullName" to registerEntity.fullName,
                                "major" to registerEntity.major,
                                "nim" to registerEntity.nim,
                                "noHp" to registerEntity.noHp,
                                "role" to UserRoleEnum.VISITOR.role,
                                "userId" to userId,
                                "training" to TrainingEnum.EMPTY.trainingName,
                                "trainingId" to ""
                            )
                        )
                    }
            }.addOnSuccessListener {
                Log.i(
                    "UserRepositoryImpl",
                    "register success"
                )
                trySend(Resource.Success(true)).isSuccess
            }.addOnFailureListener { error ->
                Log.i(
                    "UserRepositoryImpl",
                    "register failed"
                )
                trySend(Resource.Error(error.checkFirebaseError()))
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun login(email: String, password: String): Flow<Resource<Boolean>> =
        callbackFlow {
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

    override suspend fun editUser(editUserEntity: EditUserEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            val snapshotListener = db.runBatch {
                val userEntity = editUserEntity.userEntity
                dbUsers.document(userEntity.userId).update(
                    mapOf(
                        "fullName" to userEntity.fullName,
                        "major" to userEntity.major,
                        "nim" to userEntity.nim,
                        "noHp" to userEntity.noHp,
                        "training" to userEntity.training,
                        "role" to userEntity.role
                    )
                )

                //upload photo
                if (editUserEntity.pathImage != "") {
                    val file = Uri.fromFile(File(editUserEntity.pathImage))
                    val storageRef = imagesRef.child(userEntity.userId)
                    storageRef.putFile(file)
                }
            }.addOnSuccessListener {
                Log.i(
                    "UserRepositoryImpl",
                    "editUser success"
                )
                trySend(Resource.Success(true)).isSuccess
            }.addOnFailureListener { error ->
                Log.i(
                    "UserRepositoryImpl",
                    "editUser failed"
                )
                trySend(Resource.Error(error.checkFirebaseError()))
            }
            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun registerTraining(trainingId: String): Flow<Resource<Boolean>> =
        callbackFlow {
//            val snapshotListener = db.runBatch {
//                val userEntity = editUserEntity.userEntity
//                dbUsers.document(userEntity.userId).update(
//                    mapOf(
//                        "fullName" to userEntity.fullName,
//                        "major" to userEntity.major,
//                        "nim" to userEntity.nim,
//                        "noHp" to userEntity.noHp,
//                        "training" to userEntity.training,
//                        "role" to userEntity.role
//                    )
//                )
//
//                //upload photo
//                if (editUserEntity.pathImage != "") {
//                    val file = Uri.fromFile(File(editUserEntity.pathImage))
//                    val storageRef = imagesRef.child(userEntity.userId)
//                    storageRef.putFile(file)
//                }
//            }.addOnSuccessListener {
//                Log.i(
//                    "UserRepositoryImpl",
//                    "editUser success"
//                )
//                trySend(Resource.Success(true)).isSuccess
//            }.addOnFailureListener { error ->
//                Log.i(
//                    "UserRepositoryImpl",
//                    "editUser failed"
//                )
//                trySend(Resource.Error(error.checkFirebaseError()))
//            }
//            awaitClose { snapshotListener.isCanceled() }
        }

    override suspend fun logout(): Flow<Resource<Boolean>> = callbackFlow {
        auth.signOut()
        trySend(Resource.Success(true)).isSuccess
    }
}