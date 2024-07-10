package com.example.multilocator

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.multilocator.components.utils.Colors
import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.screens.account.AccountInfoScreen
import com.example.multilocator.screens.home.HomeScreen
import com.example.multilocator.screens.settings.SettingScreen
import com.example.multilocator.screens.sign_in.SignInScreen
import com.example.multilocator.screens.sign_up.SignUpScreen
import com.example.multilocator.screens.splash.SplashScreen
import com.example.multilocator.ui.theme.MultiLocatorTheme

const val time = 500

@Composable
fun MultiLocatorApp() {
    MultiLocatorTheme(
        darkTheme = true,
        dynamicColor = false
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            val appState = rememberAppState()

            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.backgroundColor),
                navController = appState.navController,
                startDestination = MultiLocatorScreens.SplashScreen.name,
                enterTransition = {
                    fadeIn(animationSpec = tween(time)) +
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Left,
                                tween(time)
                            )
                },
                exitTransition = {
                    fadeOut(animationSpec = tween(time)) +
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Down,
                                tween(time)
                            )
                },
                popEnterTransition = {
                    fadeIn(animationSpec = tween(time)) +
                            slideIntoContainer(
                                AnimatedContentTransitionScope.SlideDirection.Up,
                                tween(time)
                            )
                },
                popExitTransition = {
                    fadeOut(animationSpec = tween(time)) +
                            slideOutOfContainer(
                                AnimatedContentTransitionScope.SlideDirection.Right,
                                tween(time)
                            )
                },
            ) {
                multiLocatorGraph(appState)
            }
        }
    }
}

@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        MultiLocatorAppState(navController)
    }

fun NavGraphBuilder.multiLocatorGraph(appState: MultiLocatorAppState) {

    composable(
        route = MultiLocatorScreens.HomeScreen.name,
        popEnterTransition = {
            fadeIn()
        }
    ) {
        HomeScreen(
            restartApp = { route ->
                appState.clearAndNavigate(route)
            },
            openScreen = { route ->
                appState.navigate(route)
            },
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            },
            popUp = {
                appState.pupUp()
            }
        )
    }

    composable(route = MultiLocatorScreens.SplashScreen.name) {
        SplashScreen(openAndPopUp = { route, popUp ->
            appState.navigateAndPopUp(route, popUp)
        })
    }


    composable(route = MultiLocatorScreens.SignInScreen.name) {
        SignInScreen(
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(route = MultiLocatorScreens.SignUpScreen.name) {
        SignUpScreen(
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(route = MultiLocatorScreens.SettingsScreen.name) {
        SettingScreen(
            popUp = {
                appState.pupUp()
            },
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            },
            openScreen = { route ->
                appState.navigate(route)
            },
        )
    }

    composable(route = MultiLocatorScreens.AccountInfoScreen.name) {
        AccountInfoScreen(
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            },
            openScreen = { route ->
                appState.navigate(route)
            },
            popUp = {
                appState.pupUp()
            }
        )
    }
}
