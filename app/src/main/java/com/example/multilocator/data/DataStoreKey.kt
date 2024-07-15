package com.example.multilocator.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreKey @Inject constructor(@ApplicationContext private val context: Context) {

    private companion object {
        private val Context.uniqueIdDataStore: DataStore<Preferences> by preferencesDataStore(name = "userUniqueId")
        private val Context.userNameDataStore: DataStore<Preferences> by preferencesDataStore(name = "userName")
        private val Context.currentGroupId: DataStore<Preferences> by preferencesDataStore(name = "currentGroupId")
        private val Context.isSharingLocationStore: DataStore<Preferences> by preferencesDataStore(
            name = "isSharingLocation"
        )
        private val Context.currentGroupName: DataStore<Preferences> by preferencesDataStore(
            name = "currentGroupName"
        )

        val USER_UNIQUE_ID = stringPreferencesKey("uniqueId")
        val USER_NAME = stringPreferencesKey("username")
        val CURRENT_GROUP_ID = stringPreferencesKey("groupId")
        val CURRENT_GROUP_NAME = stringPreferencesKey("groupName")
        val IS_SHARING_LOCATION = booleanPreferencesKey("isShare")
    }

    fun getUniqueId(): Flow<String?> {
        return context.uniqueIdDataStore.data.map { preferences ->
            preferences[USER_UNIQUE_ID] ?: ""
        }
    }

    suspend fun saveUniqueId(userUniqueId: String) {
        context.uniqueIdDataStore.edit { preferences ->
            preferences[USER_UNIQUE_ID] = userUniqueId
        }
    }

    fun getUserName(): Flow<String?> {
        return context.userNameDataStore.data.map { preferences ->
            preferences[USER_NAME] ?: ""
        }
    }

    suspend fun saveUserName(userName: String) {
        context.userNameDataStore.edit { preferences ->
            preferences[USER_NAME] = userName
        }
    }

    fun getGroupId(): Flow<String?> {
        return context.currentGroupId.data.map { preferences ->
            preferences[CURRENT_GROUP_ID] ?: ""
        }
    }

    suspend fun saveGroupId(groupId: String) {
        context.currentGroupId.edit { preferences ->
            preferences[CURRENT_GROUP_ID] = groupId
        }
    }

    fun getUserSharingLocation(): Flow<Boolean?> {
        return context.isSharingLocationStore.data.map { preferences ->
            preferences[IS_SHARING_LOCATION] ?: false
        }
    }

    suspend fun saveUserSharingLocation(isShare: Boolean) {
        context.isSharingLocationStore.edit { preferences ->
            preferences[IS_SHARING_LOCATION] = isShare
        }
    }

    fun getGroupName(): Flow<String?> {
        return context.currentGroupName.data.map { preferences ->
            preferences[CURRENT_GROUP_NAME] ?: ""
        }
    }

    suspend fun saveGroupName(groupName: String) {
        context.currentGroupName.edit { preferences ->
            preferences[CURRENT_GROUP_NAME] = groupName
        }
    }
}