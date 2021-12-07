package com.dncc.dncc.presentation.profil

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.common.UploadImageUseCase
import com.dncc.dncc.domain.use_case.login.LoginStateUseCase
import com.dncc.dncc.domain.use_case.logout.LogoutUseCase
import com.dncc.dncc.domain.use_case.user.EditUserUseCase
import com.dncc.dncc.domain.use_case.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase,
    private val editUserUseCase: EditUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val loginStateUseCase: LoginStateUseCase,
    private val uploadImageUseCase: UploadImageUseCase
) : ViewModel() {

    private val _getUserResponse = MutableLiveData<Resource<UserEntity>>()
    val getUserResponse: LiveData<Resource<UserEntity>> = _getUserResponse

    private val _editUserResponse = MutableLiveData<Resource<Boolean>>()
    val editUserResponse: LiveData<Resource<Boolean>> = _editUserResponse

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.i("ProfileViewModel", "logout")
            logoutUseCase()
            loginStateUseCase.setLoginState(false)
        }
    }

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase(userId)
                .onStart {
                    _getUserResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ProfileViewModel", e.toString())
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

    fun editUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            editUserUseCase(userEntity)
                .onStart {
                    _editUserResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ProfileViewModel", e.toString())
                    _editUserResponse.postValue(Resource.Error(e.toString()))
                }
                .collect {
                    if (it.data == null) {
                        _editUserResponse.postValue(Resource.Error("Maaf harap coba lagi"))
                    } else {
                        _editUserResponse.postValue(Resource.Success(data = it.data))
                    }
                }
        }
    }
}