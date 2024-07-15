package com.example.multilocator.screens.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.pager.PagerSnapDistance
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.multilocator.R
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.GroupInfo
import com.example.multilocator.model.User
import com.example.multilocator.model.UserInfo
import com.example.multilocator.screens.mapScreen.MapScreenPager
import com.example.multilocator.screens.mapScreen.MapViewModel
import com.example.multilocator.screens.mapScreen.PermissionEvent
import com.example.multilocator.screens.mapScreen.ViewState
import com.example.multilocator.screens.viewModel.LocationViewModel
import com.example.multilocator.screens.viewModel.UserUniqueIdViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    restartApp: (String) -> Unit,
    openScreen: (String) -> Unit,
    popUp: () -> Unit,
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
    userIdViewModel: UserUniqueIdViewModel = hiltViewModel(),
    locationViewModel: LocationViewModel = hiltViewModel()
) {
    val userUniqueId = userIdViewModel.uniqueId.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { 2 })
    val fling = flingBehavior(
        state = pagerState,
        pagerSnapDistance = PagerSnapDistance.atMost(2)
    )
    val viewState by mapViewModel.viewState.collectAsStateWithLifecycle()
    val currentLocation = rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }

    val isOnline = viewModel.isOnline.collectAsState(initial = false)

    val userGroups: State<DataOrException<List<String>?, Boolean, Exception>> =
        viewModel.userGroups.collectAsStateWithLifecycle()

    val userGroupsInfo: State<DataOrException<List<GroupInfo>?, Boolean, Exception>> =
        viewModel.userGroupsInfo.collectAsStateWithLifecycle()
    val user: State<DataOrException<User?, Boolean, Exception>> =
        viewModel.user.collectAsStateWithLifecycle()

    val getGroupUserWithName: State<DataOrException<Map<String, UserInfo>?, Boolean, Exception>> =
        viewModel.getGroupUserWithName.collectAsStateWithLifecycle()

    val locationR = locationViewModel.location.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = locationR.value) {
        Log.d("Composable", "Location R: ${locationR.value}")
        currentLocation.value = LatLng(
            locationR.value?.latitude ?: 0.0,
            locationR.value?.longitude ?: 0.0
        )
    }

    LaunchedEffect(key1 = isOnline.value, key2 = userUniqueId.value) {
        if (isOnline.value && userUniqueId.value.isNotEmpty()) {
            delay(300L)
            viewModel.getUserGroups(userUniqueId.value)
        }
    }

    LaunchedEffect(key1 = userGroups.value) {
        userGroups.value.data?.let { viewModel.getGroupInfo(it) }
    }


    /*Log.d("userGroups", userGroups.value.data.toString())
    Log.d("userGroupsInfo", userGroupsInfo.value.data.toString())
    Log.d("getGroupMembersWithNames", getGroupUserWithName.value.data.toString())*/

    fun hasLocationPermission(): Boolean {
        val coarseLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fineLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return coarseLocationPermission && fineLocationPermission
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    LaunchedEffect(!hasLocationPermission()) {
        permissionState.launchMultiplePermissionRequest()
    }

    when {
        permissionState.allPermissionsGranted -> {
            LaunchedEffect(Unit) {
                mapViewModel.handle(PermissionEvent.Granted)
            }
        }

        permissionState.shouldShowRationale -> {
            RationaleAlert(onDismiss = { }) {
                permissionState.launchMultiplePermissionRequest()
            }
        }

        !permissionState.allPermissionsGranted && !permissionState.shouldShowRationale -> {
            LaunchedEffect(Unit) {
                mapViewModel.handle(PermissionEvent.Revoked)
            }
        }
    }

    with(viewState) {
        when (this) {
            ViewState.Loading -> {
                CircularProgressIndicator()
            }

            ViewState.RevokedPermissions -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("We need permissions to use this app")
                    Button(
                        onClick = {
                            startActivity(context, Intent(ACTION_LOCATION_SOURCE_SETTINGS), null)
                        },
                        enabled = !hasLocationPermission()
                    ) {
                        if (hasLocationPermission()) CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color.White
                        )
                        else Text("Settings")
                    }
                }
            }

            is ViewState.Success -> {
                /*currentLocation.value = LatLng(
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                )*/
            }

            else -> {}
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        HorizontalPager(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground),
            state = pagerState,
            flingBehavior = fling,
            userScrollEnabled = false,
            beyondBoundsPageCount = 2
        ) { page ->
            when (page) {
                0 -> {
                    HomeScreenPager(
                        viewModel = viewModel,
                        mapViewModel = mapViewModel,
                        locationViewModel = locationViewModel,
                        userIdViewModel = userIdViewModel,
                        openAndPopUp = openAndPopUp,
                        openScreen = openScreen,
                        pagerState = pagerState,
                        scope = scope,
                        currentLocation = currentLocation,
                        userGroups = userGroups,
                        userGroupsInfo = userGroupsInfo,
                        getGroupUserWithName = getGroupUserWithName,
                        isOnline = isOnline,
                        userUniqueId = userUniqueId
                    )
                }

                1 -> {
                    MapScreenPager(
                        mapViewModel = mapViewModel,
                        pagerState = pagerState,
                        scope = scope,
                        currentLocation = currentLocation,
                    )
                }
            }
        }
    }

    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {}
            Lifecycle.Event.ON_START -> {}
            Lifecycle.Event.ON_RESUME -> {}
            Lifecycle.Event.ON_PAUSE -> {}
            Lifecycle.Event.ON_STOP -> {}
            Lifecycle.Event.ON_DESTROY -> {}
            Lifecycle.Event.ON_ANY -> {}
        }
    }

    //var firstBackPressTime: Long = 0
    //val activity = (LocalContext.current as? Activity)
    BackHandler {
        //val currentTime = System.currentTimeMillis()
        //val timeDelta = currentTime - firstBackPressTime
        if (pagerState.currentPage == 1) {
            scope.launch {
                pagerState.animateScrollToPage(0)
            }
        } else {
            popUp()
            /*if (timeDelta in 0..2000) {
            } else {
                firstBackPressTime = currentTime
                Toast.makeText(activity, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }*/
        }
    }
}


@Composable
fun UserRow(
    user: UserInfo,
    isGroup: Boolean = false,
    delete: () -> Unit = {}
) {

    val imageUrl = if (user.profilePic.isNullOrEmpty()) R.drawable.person else user.profilePic

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
            .padding(horizontal = 0.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Spacer(modifier = Modifier.width(10.dp))
            SubcomposeAsyncImage(
                modifier = Modifier
                    .size(35.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                loading = {

                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(25.dp)
                            .padding(8.dp),
                        strokeWidth = 1.5.dp
                    )
                },
                model = imageUrl, contentDescription = null
            )
            Text(
                fontSize = 18.sp,
                text = "   " + user.username.toString()
            )
        }

        Row {
            if (isGroup) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            delete()
                        }
                        .padding(horizontal = 10.dp),
                    imageVector = Icons.Default.Delete, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}


@Composable
fun UidSearchField(
    userId: MutableState<String>,
    onClear: () -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        label = {
            Text(
                fontSize = 13.sp,
                text = "Enter user id(Uid)"
            )
        },
        value = userId.value,
        onValueChange = {
            userId.value = it
        },
        textStyle = TextStyle(
            fontSize = 15.sp
        ),
        singleLine = true,
        maxLines = 1,
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        leadingIcon = {
            AnimatedVisibility(
                visible = userId.value.isNotEmpty()
            ) {
                IconButton(onClick = {
                    onClear()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null
                    )
                }
            }
        },
        trailingIcon = {
            IconButton(onClick = {
                onSearch()
            }) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
fun ComposableLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


@Composable
fun RationaleAlert(onDismiss: () -> Unit, onConfirm: () -> Unit) {

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
                    text = "We need location permissions to use this app",
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
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

