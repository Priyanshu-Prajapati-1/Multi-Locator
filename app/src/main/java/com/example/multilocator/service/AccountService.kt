package com.example.multilocator.service

interface AccountService {
    //val currentUser: Flow<User>
    val currentUserId: String
    fun hasUser(): Boolean
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun logout()
    suspend fun deleteAccount()
}