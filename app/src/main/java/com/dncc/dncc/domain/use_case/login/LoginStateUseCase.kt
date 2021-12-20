package com.dncc.dncc.domain.use_case.login

import com.dncc.dncc.data.source.local.DataStoreManager
import javax.inject.Inject

class LoginStateUseCase @Inject constructor(
    private val dataStoreManager: DataStoreManager
) {
    val loginState = dataStoreManager.loginState

    suspend fun setLoginState(isLogin: Boolean) = dataStoreManager.setLoginState(isLogin)
}