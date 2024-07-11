package com.example.multilocator.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.multilocator.R
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.GroupInfo
import com.example.multilocator.model.UserInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenPager2(
    viewModel: HomeViewModel,
    userGroups: State<DataOrException<List<String>?, Boolean, Exception>>,
    userGroupsInfo: State<DataOrException<List<GroupInfo>?, Boolean, Exception>>,
    getGroupUserWithName: State<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>,
    pagerState1: PagerState,
    onTrackGroup: (String, String) -> Unit,
) {

    val scope = rememberCoroutineScope()
    val showUsers = rememberSaveable { mutableStateOf(false) }
    val groupName = rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 3.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Start,
                text = "Your Groups"
            )

            if (userGroupsInfo.value.loading == false && userGroups.value.loading == false) {
                if (userGroupsInfo.value.data.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TextButton(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = {
                                scope.launch {
                                    pagerState1.animateScrollToPage(1)
                                }
                            }) {
                            Text(text = "Create Group")
                        }
                    }
                }
            }

            if (userGroupsInfo.value.data != null) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(items = userGroupsInfo.value.data?.reversed() ?: emptyList()) { group ->
                        GroupRow(
                            group = group,
                            showUsers = showUsers,
                            viewModel = viewModel,
                            onTrackGroup = { id, name ->
                                onTrackGroup(id, name)
                            }
                        ) { _, name ->
                            viewModel.getGroupMembersWithNames(group.id.toString())
                            groupName.value = name
                            showUsers.value = true
                        }
                    }
                }
                Spacer(modifier = Modifier.height(150.dp))
            }
        }

        ShowGroupUsers(
            getGroupUserWithName = getGroupUserWithName,
            showUser = showUsers,
            groupName = groupName,
            viewModel = viewModel,
            onDismiss = {
                showUsers.value = false
            }
        )
    }
}


@Composable
fun GroupRow(
    group: GroupInfo,
    showUsers: MutableState<Boolean>,
    viewModel: HomeViewModel,
    onTrackGroup: (String, String) -> Unit,
    onShowUsers: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row {
                Text(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    text = group.name.toString() + "   "
                )
            }

            Row(
                modifier = Modifier
                    .clickable {
                        onTrackGroup(group.id.toString(), group.name.toString())
                    }
                    .border(
                        0.4.dp,
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    text = "Track"
                )
                Icon(
                    painter = painterResource(id = R.drawable.scatter_plot),
                    contentDescription = null
                )
            }

        }
        HorizontalDivider(
            modifier = Modifier
                .padding(vertical = 6.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                text = "Group Id: " + group.id.toString()
            )
            Icon(
                modifier = Modifier
                    .clickable {
                        onShowUsers(group.id.toString(), group.name.toString())
                    },
                painter = painterResource(id = R.drawable.group),
                contentDescription = null
            )
        }
    }
}


@Composable
fun ShowGroupUsers(
    onDismiss: () -> Unit = {},
    getGroupUserWithName: State<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>,
    showUser: MutableState<Boolean>,
    groupName: MutableState<String>,
    viewModel: HomeViewModel,
) {

    AnimatedVisibility(visible = showUser.value) {

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties()
        ) {
            Surface(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    modifier = Modifier
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getGroupUserWithName.value.loading == true) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            modifier = Modifier
                                .padding(bottom = 6.dp),
                            text = "Group : ${groupName.value}"
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        getGroupUserWithName.value.data?.forEach {
                            UserRow(user = it.value, isGroup = false)
                        }
                    }


                    TextButton(onClick = {
                        showUser.value = false
                    }) {
                        Text(text = "Close")
                    }
                }
            }
        }
    }
}

