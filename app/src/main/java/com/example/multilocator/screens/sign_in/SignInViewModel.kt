package com.example.multilocator.screens.sign_in

import android.util.Log
import com.example.multilocator.components.utils.checkEmailAndPassword
import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.repository.FireBaseRepository
import com.example.multilocator.repository.UserUniqueIdRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService,
    private val userUniqueIdRepository: UserUniqueIdRepository,
    private val fireBaseRepository: FireBaseRepository
) : MultiLocatorViewModel() {

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val isValidInput = MutableStateFlow(false)
    val errorMessage = MutableStateFlow<String?>(null)

    fun updateEmail(newEmail: String) {
        email.value = newEmail
        checkEmailAndPassword()
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
        checkEmailAndPassword()
    }

    fun onSignInClicked(openAndPopUp: (String, String) -> Unit) {
        checkEmailAndPassword()
        isLoading.value = true
        launchCatching {
            val result = accountService.signIn(email.value, password.value)
            if (result.isFailure) {
                errorMessage.value = result.exceptionOrNull()?.message
                isLoading.value = false
            } else {
                if (FirebaseAuth.getInstance().uid != null) {
                    fireBaseRepository.getUserUniqueId() { id, name ->
                        launchCatching {
                            Log.d("id", "$id, $name")
                            userUniqueIdRepository.saveUserName(name = name)
                            userUniqueIdRepository.saveUserUniqueId(uniqueId = id)
                            errorMessage.value = null
                            isLoading.value = false
                            openAndPopUp(
                                MultiLocatorScreens.HomeScreen.name,
                                MultiLocatorScreens.SignInScreen.name
                            )
                        }
                    }
                }
            }
        }
    }

    fun onSignUpClicked(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(
            MultiLocatorScreens.SignUpScreen.name,
            MultiLocatorScreens.SignInScreen.name
        )
    }

    fun checkEmailAndPassword() {
        isValidInput.value = checkEmailAndPassword(email.value, password.value)
    }
}