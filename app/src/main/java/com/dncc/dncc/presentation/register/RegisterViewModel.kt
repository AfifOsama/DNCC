package com.dncc.dncc.presentation.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.register.RegisterEntity
import com.dncc.dncc.domain.use_case.register.RegisterUseCase
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
) : ViewModel() {

    private val _registerResponse = MutableLiveData<Resource<Boolean>>()
    val registerResponse: LiveData<Resource<Boolean>> get() = _registerResponse

    fun register(registerEntity: RegisterEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            registerUseCase(registerEntity)
                .onStart {
                    _registerResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("RegisterViewModel", e.toString())
                    _registerResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _registerResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }

}