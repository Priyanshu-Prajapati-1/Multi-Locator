package com.example.multilocator.screens.sign_up

import android.util.Log
import com.example.multilocator.components.utils.checkEmailAndPassword
import com.example.multilocator.components.utils.generateUniqueID
import com.example.multilocator.model.UserInfo
import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.repository.FireBaseRepository
import com.example.multilocator.repository.UserUniqueIdRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    private val fireBaseRepository: FireBaseRepository,
    private val userUniqueIdRepository: UserUniqueIdRepository
) : MultiLocatorViewModel() {

    val database = Firebase.database.reference
    val auth = FirebaseAuth.getInstance()

    val name = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    val isValidInput = MutableStateFlow(false)
    val isEqualPassword = MutableStateFlow(false)
    val onSignUpClick = MutableStateFlow(false)
    val isLoading = MutableStateFlow(false)

    val errorMessage = MutableStateFlow<String?>(null)

    fun updateName(newName: String) {
        name.value = newName
    }

    fun updateEmail(newEmail: String) {
        isValidInput.value = checkEmailAndPassword(newEmail, password.value)
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        isValidInput.value = checkEmailAndPassword(email.value, newPassword)
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        isValidInput.value = checkEmailAndPassword(email.value, newConfirmPassword)
        confirmPassword.value = newConfirmPassword
    }

    fun onSignUpClicked(openAndPopUp: (String, String) -> Unit) {
        onSignUpClick.value = true
        isLoading.value = true
        launchCatching {
            if (password.value != confirmPassword.value) {
                isEqualPassword.value = true
                isLoading.value = false
                throw Exception("Passwords do not match")
            } else {
                isEqualPassword.value = false
                Log.d("isEqualPassword", isEqualPassword.value.toString())
            }

            val result = accountService.signUp(email.value, password.value)
            if (result.isFailure) {
                errorMessage.value = result.exceptionOrNull()?.message
                isLoading.value = false
            } else {
                errorMessage.value = null

                val uniqueId = generateUniqueID()
                val userId = auth.currentUser?.uid
                val user = UserInfo(
                    mail = email.value,
                    profilePic = "",
                    uniqueId = uniqueId,
                    username = name.value
                )

                if (userId != null) {
                    fireBaseRepository.addUserToDatabase(
                        password.value,
                        user = user,
                        onSuccess = {
                            launchCatching {
                                userUniqueIdRepository.saveUserUniqueId(uniqueId)
                                userUniqueIdRepository.saveUserName(name.value)
                            }
                            isLoading.value = false
                            openAndPopUp(
                                MultiLocatorScreens.HomeScreen.name,
                                MultiLocatorScreens.SignUpScreen.name
                            )
                        },
                        onFailure = {
                            errorMessage.value = it.message
                            isLoading.value = false
                        }
                    )
                } else {
                    errorMessage.value = "Failed to retrieve user ID"
                    isLoading.value = false
                }

                /*  if (userId != null) {

                      database.child("Users").child(userId.toString()).setValue(user)
                          .addOnCompleteListener { task ->
                              if (task.isSuccessful) {
                                  isLoading.value = false
                                  openAndPopUp(
                                      MultiLocatorScreens.HomeScreen.name,
                                      MultiLocatorScreens.SignUpScreen.name
                                  )
                              }
                              Log.d("SignUpViewModel", "User saved to database - ${task.result}")
                          }
                          .addOnFailureListener { exception ->
                              Log.e(
                                  "SignUpViewModel",
                                  "Failed to save user to database - ${exception.message}"
                              )
                              errorMessage.value = exception.message
                              isLoading.value = false
                          }
                  } else {
                      errorMessage.value = "Failed to retrieve user ID"
                      isLoading.value = false
                  }*/
            }
        }
    }

    fun onSignInClicked(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(
            MultiLocatorScreens.SignInScreen.name,
            MultiLocatorScreens.SignUpScreen.name
        )
    }
}