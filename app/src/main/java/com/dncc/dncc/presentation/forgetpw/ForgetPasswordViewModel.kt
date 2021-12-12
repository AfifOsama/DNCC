package com.dncc.dncc.presentation.forgetpw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.use_case.reset_password.PasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val passwordResetUseCase: PasswordResetUseCase
) : ViewModel() {

    private val _forgetPasswordResponse = MutableLiveData<Resource<Boolean>>()
    val forgetPasswordResponse: LiveData<Resource<Boolean>> get() = _forgetPasswordResponse

    fun passwordReset(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            passwordResetUseCase(email)
                .onStart {
                    _forgetPasswordResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ForgetPasswordViewModel", e.toString())
                    _forgetPasswordResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _forgetPasswordResponse.postValue(Resource.Success(data = it.data ?: false))
                }
        }
    }
}