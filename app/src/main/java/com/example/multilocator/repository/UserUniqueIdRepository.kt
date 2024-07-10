package com.example.multilocator.repository

import com.example.multilocator.data.DataStoreKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUniqueIdRepository @Inject constructor(
    private val dataStoreKey: DataStoreKey
) {

    fun getUserUniqueId(): Flow<String?> {
        return dataStoreKey.getUniqueId("uniqueId")
    }

    suspend fun saveUserUniqueId(uniqueId: String) {
        dataStoreKey.saveUniqueId("uniqueId", uniqueId)
    }

    fun getUserName(): Flow<String?> {
        return dataStoreKey.getUserName("username")
    }

    suspend fun saveUserName(uniqueId: String) {
        dataStoreKey.saveUserName("username", uniqueId)
    }
}