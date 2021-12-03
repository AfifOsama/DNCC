package com.dncc.dncc.presentation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.data.source.local.DataStoreManager
import com.dncc.dncc.domain.use_case.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> = _loginState

    private val _loginResponse = MutableLiveData<Resource<Boolean>>()
    val loginResponse: LiveData<Resource<Boolean>> get() = _loginResponse

    init {
        viewModelScope.launch {
            dataStoreManager.loginState.collect {
                _loginState.postValue(it)
            }
        }
    }

    fun saveLoginState(isSaveLoginState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("LoginViewModel", "save loginState: $isSaveLoginState")
            dataStoreManager.setLoginState(isSaveLoginState)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(email, password)
                .onStart {
                    _loginResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("LoginViewModel", e.toString())
                    _loginResponse.postValue(Resource.Error(e.toString()))
                }
                .collect {
                    _loginResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }
}