package com.dncc.dncc.presentation.listanggota

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dncc.dncc.common.Resource
import com.dncc.dncc.common.UserRoleEnum
import com.dncc.dncc.domain.entity.user.UserEntity
import com.dncc.dncc.domain.use_case.user.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    private val _getUsersResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersResponse: LiveData<Resource<List<UserEntity>>> = _getUsersResponse

    private val _filteredUser = MutableLiveData<Resource<List<UserEntity>>>()
    val filteredUser: LiveData<Resource<List<UserEntity>>> = _filteredUser

    init {
        getUsers()
    }

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

    fun filterUsers(filter: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredUser.postValue(Resource.Loading())
            val list = _getUsersResponse.value?.data?.filter { userEntity ->
                userEntity.fullName.lowercase(Locale.getDefault()).contains(filter.lowercase(Locale.getDefault()))
            } ?: mutableListOf()
            _filteredUser.postValue(Resource.Success(data = list))
        }
    }

    fun filterUsers(filter: UserRoleEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            _filteredUser.postValue(Resource.Loading())
            val list = _getUsersResponse.value?.data?.filter { userEntity ->
                userEntity.role == filter.role
            } ?: mutableListOf()
            _filteredUser.postValue(Resource.Success(data = list))
        }
    }
}