package com.example.multilocator.screens.settings

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.multilocator.components.utils.Colors
import com.example.multilocator.screens.viewModel.LocationViewModel
import com.example.multilocator.screens.viewModel.UserUniqueIdViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel(),
    openScreen: (String) -> Unit,
    openAndPopUp: (String, String) -> Unit,
    popUp: () -> Unit
) {

    val showDialog = remember {
        mutableStateOf(false)
    }
    val isDeleteAccount = viewModel.isDeleteAccount.collectAsStateWithLifecycle()

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(text = "Settings")
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            popUp()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.backgroundColor)
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    SettingItem(
                        icon = Icons.Outlined.AccountCircle,
                        settingName = "Account"
                    ) {
                        viewModel.openAccount(openScreen)
                    }
                    SettingItem(
                        icon = Icons.AutoMirrored.Default.ExitToApp,
                        settingName = "Logout"
                    ) {
                        locationViewModel.stopLocationUpdates()
                        viewModel.emptyGroupInfo()
                        viewModel.logout(openAndPopUp)
                    }
                    SettingItem(
                        icon = Icons.Outlined.Delete,
                        settingName = "Delete Account"
                    ) {
                        showDialog.value = true
                    }
                }
            }
        }
    }

    BackHandler {
        viewModel.exitSetting(openAndPopUp)
    }

    if (showDialog.value) {
        AccountDialog(
            title = "Are you sure you want to delete your account?",
            actionText = "Delete Account",
            showDialog = showDialog,
            isDeleteAccount = isDeleteAccount
        ) {
            locationViewModel.stopLocationUpdates()
            viewModel.deleteAccount({ value ->
                showDialog.value = value
            }, openAndPopUp)
        }
    }
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    settingName: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(
                imageVector = icon,
                contentDescription = "Back"
            )
        }
        Text(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f),
            text = settingName,
        )
        IconButton(onClick = { onClick() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Back"
            )
        }
    }
}

@Composable
fun AccountDialog(
    modifier: Modifier = Modifier,
    title: String = "",
    showDialog: MutableState<Boolean>,
    actionText: String = "",
    isDeleteAccount: State<Boolean>,
    onClick: () -> Unit = {}
) {

    Dialog(
        onDismissRequest = {
            showDialog.value = false
        },
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .padding(10.dp)
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(15.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (isDeleteAccount.value) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = title)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(onClick = {
                            showDialog.value = false
                        }) {
                            Text(text = "Cancel")
                        }
                        TextButton(onClick = {
                            onClick()
                        }) {
                            Text(text = actionText)
                        }
                    }
                }
            }
        }
    }
}