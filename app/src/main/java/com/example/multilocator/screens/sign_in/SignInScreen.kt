package com.example.multilocator.screens.sign_in

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multilocator.components.utils.Colors
import com.example.multilocator.screens.sign_up.InputField
import com.example.multilocator.screens.sign_up.LoadingDialog
import com.example.multilocator.screens.sign_up.PasswordInput

@Composable
fun SignInScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel(),
) {

    val passwordFocusRequest = FocusRequester.Default
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val isLoading = viewModel.isLoading.collectAsState()
    val isValidInput = viewModel.isValidInput.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .background(Colors.backgroundColor)
                .systemBarsPadding()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .align(Alignment.TopCenter)
                    .height(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Multi Locator",
                    fontSize = 25.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    val keyBoardController = LocalSoftwareKeyboardController.current

                    InputField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = true,
                        corner = 20.dp,
                        keyboardType = KeyboardType.Email,
                        label = "Enter your email",
                        onChangeValue = { email ->
                            viewModel.updateEmail(email)
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    PasswordInput(
                        modifier = Modifier
                            .focusRequester(passwordFocusRequest)
                            .fillMaxWidth(),
                        label = "Enter your password",
                        enabled = true,
                        corner = 20.dp,
                        keyboardType = KeyboardType.Password,
                        onAction = KeyboardActions {
                            keyBoardController?.hide()
                        },
                        passwordVisibility = passwordVisibility,
                        onChangeValue = { password ->
                            viewModel.updatePassword(password)
                        }
                    )

                    errorMessage.value?.let {
                    }
                    AnimatedVisibility(visible = errorMessage.value != null) {
                        Text(
                            text = errorMessage.value ?: "",
                            color = Color.Red,
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isValidInput.value,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        onClick = {
                            viewModel.onSignInClicked(openAndPopUp)
                            keyBoardController?.hide()
                            viewModel.errorMessage.value = null
                        }) {
                        if (isLoading.value) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(25.dp)
                            )
                        } else {
                            Text(text = "Sign Up")
                        }
                    }


                }
            }
            TextButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp),
                onClick = {
                    viewModel.onSignUpClicked(openAndPopUp)
                }) {
                Text(text = "Need an account? Sign up")
            }
        }
    }

    if (isLoading.value) {
        LoadingDialog()
    }
}

