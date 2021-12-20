package com.dncc.dncc.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.reset_training.DeleteTrainingsDataUseCase
import com.dncc.dncc.domain.use_case.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val deleteTrainingsDataUseCase: DeleteTrainingsDataUseCase
) : ViewModel() {

    private val _getUserResponse = MutableLiveData<Resource<UserEntity>>()
    val getUserResponse: LiveData<Resource<UserEntity>> = _getUserResponse

    private val _deleteTrainingsDataResponse = MutableLiveData<Resource<Boolean>>()
    val deleteTrainingsDataResponse: LiveData<Resource<Boolean>> = _deleteTrainingsDataResponse

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase(userId)
                .onStart {
                    _getUserResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("HomeViewModel", e.toString())
                    _getUserResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    if (it.data == null) {
                        _getUserResponse.postValue(Resource.Error("Maaf harap coba lagi"))
                    } else {
                        _getUserResponse.postValue(Resource.Success(data = it.data))
                    }
                }
        }
    }

    fun deleteTrainingsData() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTrainingsDataUseCase()
                .onStart {
                    _deleteTrainingsDataResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("HomeViewModel", e.toString())
                    _deleteTrainingsDataResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    if (it.data == null) {
                        _deleteTrainingsDataResponse.postValue(Resource.Error("Maaf harap coba lagi"))
                    } else {
                        _deleteTrainingsDataResponse.postValue(Resource.Success(data = it.data))
                    }
                }
        }
    }
}