package com.example.multilocator.service.module

import android.content.Context
import com.example.multilocator.service.AccountService
import com.example.multilocator.service.ILocationService
import com.example.multilocator.service.impl.AccountServiceImpl
import com.example.multilocator.service.impl.LocationService
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    @Binds
    @Singleton
    abstract fun provideAccountService(impl: AccountServiceImpl): AccountService



}