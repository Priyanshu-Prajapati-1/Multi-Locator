package com.example.multilocator.di

import android.content.Context
import com.example.multilocator.repository.ConnectivityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideConnectivityRepository(@ApplicationContext context: Context): ConnectivityRepository {
        return ConnectivityRepository(context)
    }

}