package com.dncc.dncc.di

import com.dncc.dncc.data.MainRepositoryImpl
import com.dncc.dncc.domain.MainRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindMainRepository(mainRepositoryImpl: MainRepositoryImpl): MainRepository
}
