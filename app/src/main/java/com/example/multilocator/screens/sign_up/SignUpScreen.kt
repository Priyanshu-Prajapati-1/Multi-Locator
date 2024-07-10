package com.example.multilocator.screens.sign_up

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multilocator.R
import com.example.multilocator.components.utils.Colors

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val passwordFocusRequest = remember { FocusRequester() }
    val confirmPasswordFocusRequest = remember { FocusRequester() }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val isEqualPassword = viewModel.isEqualPassword.collectAsState()
    val onSignUpClick = viewModel.onSignUpClick.collectAsState()
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
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        label = "Enter your name",
                        corner = 20.dp,
                        keyboardType = KeyboardType.Text,
                        onChangeValue = { name ->
                            viewModel.updateName(name)
                        }
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    InputField(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        label = "Enter your email",
                        corner = 20.dp,
                        keyboardType = KeyboardType.Email,
                        onAction = KeyboardActions { passwordFocusRequest.requestFocus() },
                        onChangeValue = { email ->
                            viewModel.updateEmail(email)
                        }
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        text = "   email: xyz@gmail.com"
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    PasswordInput(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequest),
                        label = "Enter your password",
                        corner = 20.dp,
                        enabled = true,
                        onAction = KeyboardActions { confirmPasswordFocusRequest.requestFocus() },
                        passwordVisibility = passwordVisibility,
                        onChangeValue = { password ->
                            viewModel.updatePassword(password)
                        }
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontSize = 12.sp,
                        text = "   password: ****** (At least 6 characters)"
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    PasswordInput(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(confirmPasswordFocusRequest),
                        label = "Confirm your password",
                        corner = 20.dp,
                        enabled = true,
                        keyboardType = KeyboardType.Password,
                        onAction = KeyboardActions {
                            keyBoardController?.hide()
                        },
                        passwordVisibility = passwordVisibility,
                        onChangeValue = { confirmPassword ->
                            viewModel.updateConfirmPassword(confirmPassword)
                        }
                    )
                    AnimatedVisibility(visible = errorMessage.value != null) {
                        Text(
                            text = errorMessage.value ?: "",
                            color = Color.Red,
                            fontSize = 10.sp,
                            lineHeight = 12.sp,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 5.dp)
                        )
                    }
                    AnimatedVisibility(
                        visible = isEqualPassword.value && onSignUpClick.value
                    ) {
                        Text(
                            text = "password not match",
                            fontSize = 13.sp,
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        enabled = isValidInput.value,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        onClick = {
                            viewModel.onSignUpClicked(openAndPopUp)
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
                    viewModel.onSignInClicked(openAndPopUp)
                }) {
                Text(text = "Already have an account? Sign in")
            }
        }
    }

    if (isLoading.value) {
        LoadingDialog()
    }

}

@Composable
fun LoadingDialog(
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {},
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
            Box(
                modifier = Modifier.padding(15.dp)
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp
                )
            }
        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    label: String = "Enter your email",
    enabled: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
    corner: Dp = 0.dp,
    onChangeValue: (String) -> Unit = {}
) {

    var text by rememberSaveable { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { newValue ->
            text = newValue
            onChangeValue(text)
        },
        label = { Text(text = label) },
        modifier = modifier,
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction,
        shape = RoundedCornerShape(size = corner)
    )
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    label: String = "Enter your password",
    enabled: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Password,
    imeAction: ImeAction = ImeAction.Next,
    onAction: KeyboardActions = KeyboardActions.Default,
    passwordVisibility: MutableState<Boolean>,
    corner: Dp = 0.dp,
    onChangeValue: (String) -> Unit = {}
) {

    var password by rememberSaveable { mutableStateOf("") }
    val visualTransformation =
        if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            onChangeValue(password)
        },
        label = {
            Text(text = label)
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onAction,
        shape = RoundedCornerShape(size = corner),
        enabled = enabled,
        modifier = modifier,
        visualTransformation = visualTransformation,
        trailingIcon = {
            PasswordVisibility(passwordVisibility = passwordVisibility)
        }
    )

}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {
    val visible = passwordVisibility.value

    IconButton(
        onClick = { passwordVisibility.value = !visible },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = if (passwordVisibility.value) R.drawable.visibility else R.drawable.visibility_off),
            contentDescription = null
        )
    }
}
