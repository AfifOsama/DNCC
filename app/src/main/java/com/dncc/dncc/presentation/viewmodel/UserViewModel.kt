package com.dncc.dncc.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.user.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    val getUserResponse: LiveData<Resource<UserEntity>> = getUserUseCase.observeUser

    fun getUser(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserUseCase(userId).onStart {
                Log.i("UserViewModel", "getUser onStart")
            }.catch {
                Log.i("UserViewModel", "getUser catch")
            }.collect {
                Log.i("UserViewModel", "getUser collect")
            }
        }
    }
}