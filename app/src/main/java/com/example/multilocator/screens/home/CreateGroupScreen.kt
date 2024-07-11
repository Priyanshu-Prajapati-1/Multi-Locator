package com.example.multilocator.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import com.example.multilocator.R
import com.example.multilocator.model.UserInfo

@Composable
fun CreateGroup(
    viewModel: HomeViewModel,
    userUniqueId: State<String>,
    goToHome: () -> Unit = {}
) {

    val userInfo = viewModel.userInfo.collectAsState()

    val context = LocalContext.current
    val userList = remember { mutableStateListOf<UserInfo>() }
    val userId = rememberSaveable { mutableStateOf("") }
    val groupName = rememberSaveable { mutableStateOf("") }
    val imageUrl =
        if (userInfo.value.data?.profilePic.isNullOrEmpty()) R.drawable.person else userInfo.value.data?.profilePic
    val showDialog = remember { mutableStateOf(false) }
    val showDialogMessage = remember { mutableStateOf("") }

    val isCreatingGroup = viewModel.isCreatingGroup.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 6.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        AnimatedVisibility(visible = isCreatingGroup.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            label = {
                Text(text = "Enter Group Name")
            },
            textStyle = TextStyle(
                fontSize = 15.sp
            ),
            singleLine = true,
            value = groupName.value,
            onValueChange = {
                groupName.value = it
            }
        )


        UidSearchField(
            userId = userId,
            onClear = { userId.value = "" }
        ) {
            if (userId.value.isNotEmpty()) {
                viewModel.getUserByUniqueId(userId.value.trim())
            }
        }

        Column(
            modifier = Modifier
                .height(50.dp)
        ) {
            if (userInfo.value.loading == true) {
                CircularProgressIndicator(
                    modifier = Modifier.size(35.dp),
                    strokeWidth = 2.dp
                )
            }
            if (userInfo.value.data != null) {
                if (userInfo.value.data?.mail != null) {

                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(50))
                            .border(
                                width = 0.3.dp,
                                color = MaterialTheme.colorScheme.onBackground,
                                RoundedCornerShape(50)
                            )
                            .padding(end = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .size(35.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            model = imageUrl, contentDescription = null
                        )
                        Text(
                            text = "  ${userInfo.value.data!!.mail}"
                        )
                        Icon(
                            modifier = Modifier
                                .clickable {
                                    if (userList.size > 10) {
                                        showDialogMessage.value = "More then 10 users not allowed"
                                        showDialog.value = true
                                        return@clickable
                                    }
                                    val isUnique =
                                        userList.none { it.uniqueId == userInfo.value.data!!.uniqueId }
                                    if (isUnique) {
                                        userList.add(userInfo.value.data!!)
                                    }
                                }
                                .padding(start = 5.dp),
                            imageVector = Icons.Default.Add, contentDescription = null
                        )
                    }
                } else {
                    Text(text = "user not found")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                fontSize = 20.sp,
                textAlign = TextAlign.Start,
                text = "Users"
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = !isCreatingGroup.value,
                    onClick = {
                        if (groupName.value == "") {
                            showDialogMessage.value = "Please enter Group Name"
                            showDialog.value = true
                        } else {
                            if (userList.isEmpty()) {
                                showDialogMessage.value = "Please add Users"
                                showDialog.value = true
                            } else {

                                if (userUniqueId.value != "") {
                                    val memberIds =
                                        userList.map { it.uniqueId.toString() }.toMutableList()
                                    memberIds.add(userUniqueId.value)
                                    viewModel.createGroup(
                                        groupName = groupName.value,
                                        memberIds = memberIds.distinct()
                                    )
                                    if (!isCreatingGroup.value) {
                                        goToHome()
                                        Toast.makeText(context, "Group created", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Your uid not found",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                    }) {
                    if (isCreatingGroup.value) {
                        CircularProgressIndicator(modifier = Modifier.size(25.dp))
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.deployed_code_account_),
                            contentDescription = null
                        )
                        Text(modifier = Modifier.padding(start = 5.dp), text = "create Group")
                    }
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
                .padding(6.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(
                    0.2.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                    RoundedCornerShape(10.dp)
                )
        ) {
            userList.distinct().forEach { user ->
                UserRow(
                    user = user,
                    isGroup = true
                ) {
                    userList.remove(user)
                }
            }
        }
        if (showDialog.value) {
            ShowDialog(
                onConfirm = { showDialog.value = false },
                onDismiss = { showDialog.value = false },
                showDialog = showDialog,
                textMessage = showDialogMessage.value
            )
        }

    }
}

@Composable
fun ShowDialog(
    textMessage: String = "Unknown Error",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    showDialog: MutableState<Boolean>
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = textMessage,
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        showDialog.value = false
                        onConfirm()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("OK")
                }
            }
        }
    }
}