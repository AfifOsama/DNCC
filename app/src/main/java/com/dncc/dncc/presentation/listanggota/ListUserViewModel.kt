package com.dncc.dncc.presentation.listanggota

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.user.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    init {
        getUsers()
    }

    private val _getUsersResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersResponse: LiveData<Resource<List<UserEntity>>> = _getUsersResponse

    fun getUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase()
                .onStart {
                    _getUsersResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ListUserViewModel", e.toString())
                    _getUsersResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    _getUsersResponse.postValue(Resource.Success(data = it.data ?: mutableListOf()))

                }
        }
    }
}