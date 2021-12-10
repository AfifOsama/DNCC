package com.dncc.dncc.presentation.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.login.LoginStateUseCase
import com.dncc.dncc.domain.use_case.login.LoginUseCase
import com.dncc.dncc.domain.use_case.user.GetUserUseCase
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
    private val loginStateUseCase: LoginStateUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> = _loginState

    private val _loginResponse = MutableLiveData<Resource<Boolean>>()
    val loginResponse: LiveData<Resource<Boolean>> get() = _loginResponse

    private val _getUserResponse = MutableLiveData<Resource<UserEntity>>()
    val getUserResponse: LiveData<Resource<UserEntity>> = _getUserResponse

    init {
        viewModelScope.launch {
            loginStateUseCase.loginState.collect {
                _loginState.postValue(it)
            }
        }
    }

    fun saveLoginState(isSaveLoginState: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("LoginViewModel", "save loginState: $isSaveLoginState")
            loginStateUseCase.setLoginState(isSaveLoginState)
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

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase(userId)
                .onStart {
                    _getUserResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("LoginViewModel", e.toString())
                    _getUserResponse.postValue(Resource.Error(e.toString()))
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
}