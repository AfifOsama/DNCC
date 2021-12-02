package com.dncc.dncc.data

import android.net.Uri
import android.util.Log
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.domain.MainRepository
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

//    override suspend fun getUsers(): Flow<FlowState<List<UserEntity>>> = callbackFlow {
//        val snapshotListener = db
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) throw e
//                val response = if (snapshot != null) {
//                    val dataUsers = mutableListOf<UserDto>()
//                    snapshot.forEach {
//                        val field = it.toObject(UserDto::class.java)
//                        dataUsers.add(field)
//                    }
//                    FlowState.Data(dataUsers.map { it.toUser() })
//                } else {
//                    FlowState.Error("")
//                }
//                trySend(response).isSuccess
//            }
//        awaitClose { snapshotListener.remove() }
//    }

    override suspend fun register(registerEntity: RegisterEntity): Flow<Resource<Boolean>> =
        callbackFlow {
            auth.createUserWithEmailAndPassword(registerEntity.email, registerEntity.password)
                .addOnCompleteListener {
                    val response = if (it.isSuccessful) {
                        val userId = auth.currentUser?.uid ?: ""
                        if (uploadImage(registerEntity.photoPath, userId)) {
                            registerToFirestore(registerEntity)
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                    if (response) {
                        trySend(Resource.Success(true)).isSuccess
                    } else {
                        trySend(Resource.Error(it.exception?.message ?: "Gagal mendaftarkan akun")).isFailure
                    }
                }
        }

    private fun uploadImage(path: String, userId: String): Boolean {
        var responseUploadImage = false
        val file = Uri.fromFile(File(path))
        val riversRef = imagesRef.child(userId)
        val uploadTask = riversRef.putFile(file)
        uploadTask.addOnSuccessListener {
            responseUploadImage = it.task.isSuccessful
            Log.d("image", "uploadImage: success")
        }
        return responseUploadImage
    }

    private fun registerToFirestore(registerEntity: RegisterEntity): Boolean {
        val userId = auth.currentUser?.uid
        var success = false
        dbUsers.document(userId ?: "").set(
            mapOf(
                "email" to registerEntity.email,
                "fullName" to registerEntity.fullName,
                "major" to registerEntity.major,
                "nim" to registerEntity.nim,
                "noHp" to registerEntity.noHp,
                "role" to UserRoleEnum.MEMBER.role,
                "userId" to userId,
            )
        ).addOnSuccessListener {
            success = true
        }
        return success
    }

    override suspend fun login(email: String, password: String): Flow<Resource<Boolean>> {
        TODO("Not yet implemented")
    }

}