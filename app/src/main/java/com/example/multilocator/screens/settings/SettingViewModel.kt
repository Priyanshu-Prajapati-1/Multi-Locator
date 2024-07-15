package com.example.multilocator.screens.settings

import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.repository.UserUniqueIdRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val accountService: AccountService,
    private val userUniqueIdRepository: UserUniqueIdRepository
) : MultiLocatorViewModel() {

    private val _isDeleteAccount: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDeleteAccount: StateFlow<Boolean> = _isDeleteAccount


    fun logout(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.logout()
            openAndPopUp(
                MultiLocatorScreens.SignInScreen.name,
                MultiLocatorScreens.HomeScreen.name
            )
        }
    }

    fun openAccount(openScreen: (String) -> Unit) {
        openScreen(MultiLocatorScreens.AccountInfoScreen.name)
    }

    fun deleteAccount(onComplete: (Boolean) -> Unit, openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            _isDeleteAccount.value = true
            accountService.deleteAccount()
            onComplete(false)
            openAndPopUp(
                MultiLocatorScreens.SignInScreen.name,
                MultiLocatorScreens.HomeScreen.name
            )
        }
    }

    fun exitSetting(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(
            MultiLocatorScreens.HomeScreen.name,
            MultiLocatorScreens.SettingsScreen.name
        )
    }

    fun emptyGroupInfo() {
        launchCatching {
            userUniqueIdRepository.saveGroupId("").apply {
                userUniqueIdRepository.saveGroupName("")
            }
        }
    }
}