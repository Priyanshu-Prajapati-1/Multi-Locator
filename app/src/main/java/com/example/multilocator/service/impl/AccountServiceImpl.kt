package com.example.multilocator.service.impl

import com.example.multilocator.service.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AccountServiceImpl @Inject constructor() : AccountService {

    override val currentUserId: String
        get() = Firebase.auth.currentUser?.uid.orEmpty()

    override fun hasUser(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("User not found"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("Invalid credentials"))
        } catch (e: FirebaseAuthException) {
            //Log.d("Error", e.message.toString())
            if (e.message?.contains("temporarily disabled due to many failed login attempts") == true) {
                Result.failure(Exception("Account temporarily disabled due to too many failed login attempts. Try resetting your password or try again later."))
            } else if (e.message?.contains("blocked all requests from this device due to unusual activity") == true) {
                Result.failure(Exception("We have blocked all requests from this device due to unusual activity. Try again later after some time."))
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("We have blocked all requests from this device due to unusual activity. Try again later after some time."))
        }
    }

    override suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthWeakPasswordException) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("Weak password"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("Invalid email"))
        } catch (e: FirebaseAuthUserCollisionException) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("Email already in use"))
        } catch (e: FirebaseAuthException) {
            //Log.d("Error", e.message.toString())
            if (e.message?.contains("temporarily disabled due to many failed login attempts") == true) {
                Result.failure(Exception("Account temporarily disabled due to too many failed login attempts. Try resetting your password or try again later."))
            } else if (e.message?.contains("blocked all requests from this device due to unusual activity") == true) {
                Result.failure(Exception("We have blocked all requests from this device due to unusual activity. Try again later after some time."))
            } else {
                Result.failure(Exception("Authentication failed"))
            }
        } catch (e: Exception) {
            //Log.d("Error", e.message.toString())
            Result.failure(Exception("We have blocked all requests from this device due to unusual activity. Try again later after some time."))
        }
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }

    override suspend fun deleteAccount() {
        Firebase.auth.currentUser!!.delete().await()
    }
}