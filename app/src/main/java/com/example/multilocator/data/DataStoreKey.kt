package com.example.multilocator.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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

    companion object {
        private val Context.uniqueIdDataStore: DataStore<Preferences> by preferencesDataStore(name = "userUniqueId")
        private val Context.userNameDataStore: DataStore<Preferences> by preferencesDataStore(name = "userName")
        private val Context.userLastLocation: DataStore<Preferences> by preferencesDataStore(name = "userLastLocation")
    }

    fun getUniqueId(key: String): Flow<String?> {
        return context.uniqueIdDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: ""
        }
    }

    suspend fun saveUniqueId(key: String, value: String) {
        context.uniqueIdDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    fun getUserName(key: String): Flow<String?> {
        return context.userNameDataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: ""
        }
    }

    suspend fun saveUserName(key: String, value: String) {
        context.userNameDataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    fun getUserLastLocation(key: String): Flow<String?> {
        return context.userLastLocation.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: ""
        }
    }

    suspend fun saveUserLastLocation(key: String, value: String) {
        context.userLastLocation.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }
}