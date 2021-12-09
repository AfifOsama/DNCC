package com.dncc.dncc.di

import com.dncc.dncc.data.TrainingRepositoryImpl
import com.dncc.dncc.data.UserRepositoryImpl
import com.dncc.dncc.domain.TrainingRepository
import com.dncc.dncc.domain.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @ExperimentalCoroutinesApi
    @Binds
    @Singleton
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @ExperimentalCoroutinesApi
    @Binds
    @Singleton
    abstract fun bindTrainingRepository(trainingRepositoryImpl: TrainingRepositoryImpl): TrainingRepository
}
