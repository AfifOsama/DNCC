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
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {

    init {
        getUsersByVisitor(UserRoleEnum.VISITOR)
        getUsersByMember(UserRoleEnum.MEMBER)
        getUsersByMentor(UserRoleEnum.MENTOR)
        getUsersByAdmin(UserRoleEnum.ADMIN)
    }

    private val _getUsersVisitorResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersVisitorResponse: LiveData<Resource<List<UserEntity>>> = _getUsersVisitorResponse

    private val _getUsersMemberResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersMemberResponse: LiveData<Resource<List<UserEntity>>> = _getUsersMemberResponse

    private val _getUsersMentorResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersMentorResponse: LiveData<Resource<List<UserEntity>>> = _getUsersMentorResponse

    private val _getUsersAdminResponse = MutableLiveData<Resource<List<UserEntity>>>()
    val getUsersAdminResponse: LiveData<Resource<List<UserEntity>>> = _getUsersAdminResponse

    fun getUsersByVisitor(filter: UserRoleEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase()
                .onStart {
                    _getUsersVisitorResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ListUserViewModel", e.toString())
                    _getUsersVisitorResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    val list: List<UserEntity> = it.data?.filter { user ->
                        user.role == filter.role
                    } ?: mutableListOf()
                    _getUsersVisitorResponse.postValue(Resource.Success(data = list))
                }
        }
    }

    fun getUsersByMember(filter: UserRoleEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase()
                .onStart {
                    _getUsersMemberResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ListUserViewModel", e.toString())
                    _getUsersMemberResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    val list: List<UserEntity> = it.data?.filter { user ->
                        user.role == filter.role
                    } ?: mutableListOf()
                    _getUsersMemberResponse.postValue(Resource.Success(data = list))
                }
        }
    }

    fun getUsersByMentor(filter: UserRoleEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase()
                .onStart {
                    _getUsersMentorResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ListUserViewModel", e.toString())
                    _getUsersMentorResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    val list: List<UserEntity> = it.data?.filter { user ->
                        user.role == filter.role
                    } ?: mutableListOf()
                    _getUsersMentorResponse.postValue(Resource.Success(data = list))
                }
        }
    }

    fun getUsersByAdmin(filter: UserRoleEnum) {
        viewModelScope.launch(Dispatchers.IO) {
            getUsersUseCase()
                .onStart {
                    _getUsersAdminResponse.postValue(Resource.Loading())
                }
                .catch { e ->
                    Log.i("ListUserViewModel", e.toString())
                    _getUsersAdminResponse.postValue(Resource.Error("${e.message}"))
                }
                .collect {
                    val list: List<UserEntity> = it.data?.filter { user ->
                        user.role == filter.role
                    } ?: mutableListOf()
                    _getUsersAdminResponse.postValue(Resource.Success(data = list))
                }
        }
    }
}