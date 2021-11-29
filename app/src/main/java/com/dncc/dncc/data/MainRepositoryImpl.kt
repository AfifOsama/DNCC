package com.dncc.dncc.data

import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.MainRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MainRepositoryImpl @Inject constructor() : MainRepository {
    private val db = Firebase.firestore.collection("users")

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

    override suspend fun login(): Flow<Resource<String>> {
        TODO("Not yet implemented")
    }
}