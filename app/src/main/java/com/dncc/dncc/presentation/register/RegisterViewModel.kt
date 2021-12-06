package com.dncc.dncc.presentation.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.domain.use_case.register.RegisterFirestoreUseCase
import com.dncc.dncc.domain.use_case.register.RegisterUseCase
import com.dncc.dncc.domain.use_case.common.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val registerFirestoreUseCase: RegisterFirestoreUseCase
) : ViewModel() {

    private val _registerResponse = MutableLiveData<Resource<String>>()
    val registerResponse: LiveData<Resource<String>> get() = _registerResponse

    private val _uploadImageResponse = MutableLiveData<Resource<Boolean>>()
    val uploadImageResponse: LiveData<Resource<Boolean>> get() = _uploadImageResponse

    private val _registerFirestoreResponse = MutableLiveData<Resource<Boolean>>()
    val registerFirestoreResponse: LiveData<Resource<Boolean>> get() = _registerFirestoreResponse

    fun register(registerEntity: RegisterEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUseCase(registerEntity)
                .onStart {
                    _registerResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("RegisterViewModel", e.toString())
                    _registerResponse.postValue(Resource.Error(e.toString()))
                }
                .collect {
                    _registerResponse.postValue(Resource.Success(data = it.data ?: ""))
                }
        }
    }

    fun uploadImage(path: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uploadImageUseCase(path, userId)
                .onStart {
                    _uploadImageResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("RegisterViewModel", e.toString())
                    _uploadImageResponse.postValue(Resource.Error(e.toString()))
                }
                .collect {
                    _uploadImageResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

    fun registerFirestore(registerEntity: RegisterEntity, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            registerFirestoreUseCase(registerEntity, userId)
                .onStart {
                    _registerFirestoreResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("RegisterViewModel", e.toString())
                    _registerFirestoreResponse.postValue(Resource.Error(e.toString()))
                }
                .collect {
                    _registerFirestoreResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

}