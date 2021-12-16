package com.dncc.dncc.presentation.pertemuan

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.training.MeetEntity
import com.dncc.dncc.domain.entity.training.TrainingEntity
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.training.meets.GetMeetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeetViewModel @Inject constructor(
    private val getMeetUseCase: GetMeetUseCase
): ViewModel() {

    private val _getMeetResponse = MutableLiveData<Resource<MeetEntity>>()
    val getMeetResponse: LiveData<Resource<MeetEntity>> = _getMeetResponse

    fun addTraining(trainingId: String, meetId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getMeetUseCase(trainingId, meetId)
                .onStart {
                    _getMeetResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("MeetViewModel", e.toString())
                    _getMeetResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _getMeetResponse.postValue(Resource.Success(data = it.data ?: MeetEntity()))
                }
        }
    }
}