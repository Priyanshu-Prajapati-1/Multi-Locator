package com.example.multilocator.repository

import android.util.Log
import com.example.multilocator.data.DataStoreKey
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserUniqueIdRepository @Inject constructor(
    private val dataStoreKey: DataStoreKey
) {

    fun getUserUniqueId(): Flow<String?> {
        return dataStoreKey.getUniqueId()
    }

    suspend fun saveUserUniqueId(uniqueId: String) {
        dataStoreKey.saveUniqueId(uniqueId)
    }

    fun getUserName(): Flow<String?> {
        return dataStoreKey.getUserName()
    }

    suspend fun saveUserName(name: String) {
        dataStoreKey.saveUserName(name)
    }

    fun getGroupId(): Flow<String?> {
        return dataStoreKey.getGroupId()
    }

    suspend fun saveGroupId(groupId: String) {
        dataStoreKey.saveGroupId(groupId)
    }

    fun getGroupName(): Flow<String?> {
        return dataStoreKey.getGroupName()
    }

    suspend fun saveGroupName(groupName: String) {
        dataStoreKey.saveGroupName(groupName)
    }

    fun getUserSharingLocation(): Flow<Boolean?> {
        return dataStoreKey.getUserSharingLocation()
    }

    suspend fun saveUserSharingLocation(isShare: Boolean) {
        dataStoreKey.saveUserSharingLocation(isShare)
    }
}