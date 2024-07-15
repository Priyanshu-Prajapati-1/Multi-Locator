package com.example.multilocator.screens.account

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.example.multilocator.R
import com.example.multilocator.components.utils.Colors
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.UserInfo
import com.example.multilocator.screens.viewModel.LocationViewModel
import com.example.multilocator.screens.viewModel.UserUniqueIdViewModel
import com.google.firebase.storage.FirebaseStorage

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountInfoScreen(
    modifier: Modifier = Modifier,
    openAndPopUp: (String, String) -> Unit,
    openScreen: (String) -> Unit,
    popUp: () -> Unit,
    viewModel: AccountInfoViewModel = hiltViewModel(),
    userIdViewModel: UserUniqueIdViewModel = hiltViewModel(),
) {

    val userUniqueId = userIdViewModel.uniqueId.collectAsStateWithLifecycle()

    val storage = FirebaseStorage.getInstance()
    //val userId = FirebaseAuth.getInstance().uid
    val userInfo: State<DataOrException<UserInfo?, Boolean, Exception>> =
        viewModel.userInfo.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = userUniqueId.value) {
        if (userUniqueId.value != "") {
            viewModel.getUserByUniqueId(uniqueId = userUniqueId.value)
        }
    }

    val imageUrl =
        if (userInfo.value.data?.profilePic.isNullOrEmpty()) R.drawable.person else userInfo.value.data?.profilePic

    val showUpdateImageDialog = remember {
        mutableStateOf(false)
    }

    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                showUpdateImageDialog.value = true
            }
            imageUri.value = uri
        }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            popUp()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = null
                            )
                        }
                    },
                    title = {
                        Text(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                            text = "Profile"
                        )
                    })
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.backgroundColor)
                    .padding(innerPadding)
            ) {
                if (userInfo.value.data != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(15.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(15.dp)
                            )
                            .padding(15.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box {
                            SubcomposeAsyncImage(
                                modifier = Modifier
                                    .size(75.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                model = imageUrl,
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.padding(15.dp),
                                        strokeWidth = 2.dp
                                    )
                                },
                                contentDescription = null
                            )
                            Icon(
                                modifier = Modifier
                                    .clickable { launcher.launch("image/*") }
                                    .align(Alignment.BottomEnd)
                                    .size(23.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                                    .padding(2.5.dp)
                                    .border(
                                        1.dp,
                                        MaterialTheme.colorScheme.onBackground,
                                        CircleShape
                                    ),
                                imageVector = Icons.Default.Add, contentDescription = null
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                .padding(5.dp)
                        ) {
                            Text(text = userInfo.value.data?.username.toString())
                            Text(text = userInfo.value.data?.mail.toString())
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        CopyableText(text = userInfo.value.data?.uniqueId.toString())

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {

                        }

                        if (showUpdateImageDialog.value) {
                            UploadImageDialog(
                                imageUri = imageUri,
                                viewModel = viewModel,
                                userUniqueId = userUniqueId,
                                showUpdateImageDialog = showUpdateImageDialog
                            ) {
                                showUpdateImageDialog.value = false
                            }
                        }
                    }
                } else {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        }

        BackHandler {
            popUp()
        }
    }
}

@Composable
fun UploadImageDialog(
    imageUri: MutableState<Uri?>,
    viewModel: AccountInfoViewModel,
    showUpdateImageDialog: MutableState<Boolean>,
    userUniqueId: State<String>,
    onDismiss: () -> Unit,
) {

    val isImageUpdate = viewModel.isImageUpdate.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .wrapContentSize(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    model = imageUri.value,
                    contentDescription = null
                )

                TextButton(
                    enabled = !isImageUpdate.value,
                    onClick = {
                        viewModel.updateImage(
                            userUniqueId.value,
                            imageUri.value
                        ) { isUpdateComplete ->
                            if (isUpdateComplete) {
                                showUpdateImageDialog.value = false
                            }
                        }
                    }) {
                    if (isImageUpdate.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(30.dp),
                        )
                    } else {
                        Text(text = "Update")
                    }
                }
            }
        }
    }
}

@Composable
fun CopyableText(
    text: String,
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row {
        Text(
            fontSize = 12.sp,
            text = "Uid: $text"
        )
        Icon(
            modifier = Modifier
                .clickable {
                    clipboardManager.setText(AnnotatedString(text))
                    Toast
                        .makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT)
                        .show()
                }
                .size(20.dp),
            painter = painterResource(id = R.drawable.content_copy),
            contentDescription = null
        )
    }

}
