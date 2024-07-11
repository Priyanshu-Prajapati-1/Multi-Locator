package com.example.multilocator

import androidx.compose.runtime.Stable
import androidx.navigation.NavHostController

@Stable
class MultiLocatorAppState (val navController: NavHostController){
    fun pupUp(){
        navController.popBackStack()
    }

    fun navigate(route: String){
        navController.navigate(route){
            launchSingleTop = true
        }
    }

    fun navigateAndPopUp(route: String, popUp: String){
        navController.navigate(route){
            launchSingleTop = true
            popUpTo(popUp){
                inclusive = true
            }
        }
    }

    fun clearAndNavigate(route: String){
        navController.navigate(route){
            launchSingleTop = true
            popUpTo(0){
                inclusive = true
            }
        }
    }
}