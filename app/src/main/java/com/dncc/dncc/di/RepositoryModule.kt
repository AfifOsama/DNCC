package com.dncc.dncc.di

import com.dncc.dncc.data.TrainingRepositoryImpl
import com.dncc.dncc.data.UserRepositoryImpl
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.UserRepository
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
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindTrainingRepository(trainingRepositoryImpl: TrainingRepositoryImpl): TrainingRepository
}
