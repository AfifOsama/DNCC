package com.dncc.dncc.presentation.pelatihan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.training.*
import com.dncc.dncc.domain.use_case.training.meets.GetMeetsUseCase
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
    private val getTrainingsUseCase: GetTrainingsUseCase,
    private val getTrainingUseCase: GetTrainingUseCase,
    private val deleteTrainingUseCase: DeleteTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    private val getMeetsUseCase: GetMeetsUseCase,
    private val getTrainingParticipantsUseCase: GetParticipantsUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _addTrainingResponse = MutableLiveData<Resource<Boolean>>()
    val addTrainingResponse: LiveData<Resource<Boolean>> = _addTrainingResponse

    private val _getTrainingsResponse = MutableLiveData<Resource<List<TrainingEntity>>>()
    val getTrainingsResponse: LiveData<Resource<List<TrainingEntity>>> = _getTrainingsResponse

    private val _getTrainingResponse = MutableLiveData<Resource<TrainingEntity>>()
    val getTrainingResponse: LiveData<Resource<TrainingEntity>> = _getTrainingResponse

    private val _deleteTrainingsResponse = MutableLiveData<Resource<Boolean>>()
    val deleteTrainingsResponse: LiveData<Resource<Boolean>> = _deleteTrainingsResponse

    private val _editTrainingResponse = MutableLiveData<Resource<Boolean>>()
    val editTrainingResponse: LiveData<Resource<Boolean>> = _editTrainingResponse

    private val _getMeetsResponse = MutableLiveData<Resource<List<MeetEntity>>>()
    val getMeetsResponse: LiveData<Resource<List<MeetEntity>>> = _getMeetsResponse

    private val _getTrainingParticipants = MutableLiveData<Resource<List<UserEntity>>>()
    val getTrainingParticipants: LiveData<Resource<List<UserEntity>>> = _getTrainingParticipants

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
                    _addTrainingResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

    fun getTrainings() {
        viewModelScope.launch(Dispatchers.IO) {
            getTrainingsUseCase()
                .onStart {
                    _getTrainingsResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _getTrainingsResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _getTrainingsResponse.postValue(
                        Resource.Success(
                            data = it.data ?: mutableListOf()
                        )
                    )
                }
        }
    }

    fun getTraining(trainingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getTrainingUseCase(trainingId)
                .onStart {
                    _getTrainingResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _getTrainingResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _getTrainingResponse.postValue(
                        Resource.Success(
                            data = it.data ?: TrainingEntity()
                        )
                    )
                }
        }
    }

    fun deleteTraining(trainingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteTrainingUseCase(trainingId)
                .onStart {
                    _deleteTrainingsResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _deleteTrainingsResponse.postValue(Resource.Error(""))
                }
                .collect {
                    _deleteTrainingsResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

    fun editTraining(trainingEntity: TrainingEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            editTrainingUseCase(trainingEntity)
                .onStart {
                    _editTrainingResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _editTrainingResponse.postValue(Resource.Error("Gagal Maaf coba lagi"))
                }
                .collect {
                    _editTrainingResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

    fun getMeets(trainingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getMeetsUseCase(trainingId)
                .onStart {
                    _getMeetsResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _getMeetsResponse.postValue(Resource.Error(""))
                }
                .collect {
                    _getMeetsResponse.postValue(Resource.Success(data = it.data ?: mutableListOf()))
                }
        }
    }

    fun getTrainingParticipants(trainingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getTrainingParticipantsUseCase(trainingId)
                .onStart {
                    _getTrainingParticipants.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("TrainingViewModel", e.toString())
                    _getTrainingParticipants.postValue(Resource.Error(""))
                }
                .collect {
                    _getTrainingParticipants.postValue(Resource.Success(data = it.data ?: mutableListOf()))
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