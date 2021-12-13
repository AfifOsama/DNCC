package com.dncc.dncc.presentation.pelatihan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.training.AddTrainingUseCase
import com.dncc.dncc.domain.use_case.training.GetTrainingsUseCase
import com.dncc.dncc.domain.use_case.training.meets.AddMeetsUseCase
import com.dncc.dncc.domain.use_case.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val addTrainingUseCase: AddTrainingUseCase,
    private val addMeetsUseCase: AddMeetsUseCase,
    private val getTrainingsUseCase: GetTrainingsUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _addTrainingResponse = MutableLiveData<Resource<String>>()
    val addTrainingResponse: LiveData<Resource<String>> = _addTrainingResponse

    private val _addMeetsResponse = MutableLiveData<Resource<Boolean>>()
    val addMeetsResponse: LiveData<Resource<Boolean>> = _addMeetsResponse

    private val _getUserResponse = MutableLiveData<Resource<UserEntity>>()
    val getUserResponse: LiveData<Resource<UserEntity>> = _getUserResponse

    fun addTraining(trainingEntity: TrainingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            addTrainingUseCase(trainingEntity)
                .onStart {
                    _addTrainingResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _addTrainingResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _addTrainingResponse.postValue(Resource.Success(data = it.data ?: ""))
                }
        }
    }

    fun addMeets(trainingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            addMeetsUseCase(trainingId)
                .onStart {
                    _addMeetsResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _addMeetsResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _addMeetsResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase(userId)
                .onStart {
                    _getUserResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _getUserResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _getUserResponse.postValue(Resource.Success(data = it.data ?: UserEntity()))
                }
        }
    }
}