package com.example.multilocator.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multilocator.components.utils.Colors
import com.example.multilocator.components.utils.ShadedBox
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.GroupInfo
import com.example.multilocator.model.UserInfo
import com.example.multilocator.screens.mapScreen.MapViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreenPager(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
    mapViewModel: MapViewModel,
    openAndPopUp: (String, String) -> Unit,
    pagerState: PagerState,
    scope: CoroutineScope,
    openScreen: (String) -> Unit,
    currentLocation: MutableState<LatLng>,
    userGroups: State<DataOrException<List<String>?, Boolean, Exception>>,
    userGroupsInfo: State<DataOrException<List<GroupInfo>?, Boolean, Exception>>,
    getGroupUserWithName: State<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>,
    isOnline: State<Boolean>,
    userUniqueId: State<String>,
    groupInfo: (String, String) -> Unit,
) {

    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .size(50.dp),
                shape = CircleShape,
                containerColor = Color.Green.copy(alpha = 0.2f),
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowForward,
                    contentDescription = null
                )
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        text = "Multi Locator"
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    Row {
                        IconButton(onClick = {
                            viewModel.openSettings(openScreen)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "Settings"
                            )
                        }
                        IconButton(onClick = {
                            Toast.makeText(context, "Not Implemented Yet", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->


        val pagerState1 = rememberPagerState(pageCount = { 2 })
        val fling = flingBehavior(
            state = pagerState1,
            pagerSnapDistance = PagerSnapDistance.atMost(2)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.backgroundColor)
                .padding(innerPadding)
        ) {

            AnimatedVisibility(visible = !isOnline.value) {
                ShadedBox(text = "No Internet Connection")
            }

            HorizontalPagerRowHeading(
                scope = scope,
                pagerState1 = pagerState1
            )

            AnimatedVisibility(visible = userGroups.value.loading == true || userGroupsInfo.value.loading == true) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalPager(
                state = pagerState1,
                modifier = Modifier.fillMaxSize(),
                flingBehavior = fling,
                beyondBoundsPageCount = 2
            ) { page ->
                when (page) {
                    0 -> {
                        HomeScreenPager2(
                            viewModel = viewModel,
                            userGroups = userGroups,
                            userGroupsInfo = userGroupsInfo,
                            getGroupUserWithName = getGroupUserWithName,
                            pagerState1 = pagerState1,
                        ) { id, name ->
                            groupInfo(id, name)
                            viewModel.getGroupMembersWithNames(id)
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    }

                    1 -> {
                        CreateGroup(
                            viewModel = viewModel,
                            userUniqueId = userUniqueId,
                        ) {
                            scope.launch {
                                pagerState1.animateScrollToPage(0)
                            }
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalPagerRowHeading(pagerState1: PagerState, scope: CoroutineScope) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .clickable {
                    scope.launch {
                        pagerState1.animateScrollToPage(0)
                    }
                }
                .weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            text = "Home",
            color = if (pagerState1.currentPage == 0) Color.Green else MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.8f
            )
        )
        Text(
            modifier = Modifier
                .clickable {
                    scope.launch {
                        pagerState1.animateScrollToPage(1)
                    }
                }
                .weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 15.sp,
            text = "Create Group",
            color = if (pagerState1.currentPage == 1) Color.Green else MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.8f
            )
        )
    }
}

