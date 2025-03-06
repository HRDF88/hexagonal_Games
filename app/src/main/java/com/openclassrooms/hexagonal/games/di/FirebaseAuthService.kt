package com.openclassrooms.hexagonal.games.di

import com.openclassrooms.hexagonal.games.data.service.firebase.FirebaseAuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FirebaseAuthModule {

    @Provides
    fun provideFirebaseAuthService(): FirebaseAuthService {
        return FirebaseAuthService()
    }
}