package com.example.multilocator.screens.splash

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(private val accountService: AccountService) :
    MultiLocatorViewModel() {

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
            launchCatching {
                if (accountService.hasUser()) {
                    openAndPopUp(
                        MultiLocatorScreens.HomeScreen.name,
                        MultiLocatorScreens.SplashScreen.name
                    )
                } else {
                    openAndPopUp(
                        MultiLocatorScreens.SignInScreen.name,
                        MultiLocatorScreens.SplashScreen.name
                    )
                }
            }
    }
}
