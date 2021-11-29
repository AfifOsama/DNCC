package com.dncc.dncc.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore

    private val LOGIN_KEY = booleanPreferencesKey("login_state")

    suspend fun setLoginState(isLogin: Boolean) {
        settingsDataStore.edit { preferences ->
            preferences[LOGIN_KEY] = isLogin
        }
    }

    val loginState: Flow<Boolean> = settingsDataStore.data.map { preferences ->
        preferences[LOGIN_KEY] ?: false
    }

}
