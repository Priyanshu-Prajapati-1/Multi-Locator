package com.example.multilocator.screens.mapScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.multilocator.R
import com.example.multilocator.components.utils.isLocationEnabled
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.UserInfo
import com.example.multilocator.screens.viewModel.UserUniqueIdViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreenPager(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    pagerState: PagerState,
    mapViewModel: MapViewModel,
    currentLocation: MutableState<LatLng>,
    userUniqueIdViewModel: UserUniqueIdViewModel = hiltViewModel()
) {

    val uniqueId = userUniqueIdViewModel.uniqueId.collectAsStateWithLifecycle()
    val username = userUniqueIdViewModel.username.collectAsStateWithLifecycle()
    val currentGroupId = userUniqueIdViewModel.groupId.collectAsStateWithLifecycle()
    val isSharingLocation = userUniqueIdViewModel.isSharingLocation.collectAsStateWithLifecycle()
    val userLocationsList = mapViewModel.userLocations.collectAsStateWithLifecycle()

    Log.d(
        "M id, groupId, username, isSharingLocation",
        "MapScreenPager: ${uniqueId.value}, ${currentGroupId.value}, ${username.value}, ${isSharingLocation.value}"
    )

    // Log.d("id , name", "MapScreenPager: ${uniqueId.value}, ${username.value}")
    //Log.d("location", "MapScreenPager: ${userLocationsList.value}")

    val mapProperty = remember { mutableStateOf(MapProperties(mapType = MapType.HYBRID)) }
    val mapUiSetting = remember { mutableStateOf(MapUiSettings()) }
    val showDialog = remember { mutableStateOf(false) }
    val showMapMenu = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val defaultCameraPosition by remember {
        mutableStateOf(CameraPosition.fromLatLngZoom(currentLocation.value, 1f))
    }
    val cameraPositionState = rememberCameraPositionState {
        position = defaultCameraPosition
    }

    val userInfo: State<DataOrException<UserInfo?, Boolean, Exception>> =
        mapViewModel.userInfo.collectAsStateWithLifecycle()
    val groupName: State<String> =
        mapViewModel.groupName.collectAsState()
    val groupId: State<String> =
        mapViewModel.groupId.collectAsState()

    fun updateCameraPosition(newPosition: LatLng, durationMs: Int = 1000) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(newPosition, 4f)
        scope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newCameraPosition(
                    CameraPosition(newPosition, 20f, 30f, 45f)
                ),
                durationMs = durationMs
            )
        }
    }

    LaunchedEffect(key1 = currentLocation.value, key2 = uniqueId.value) {
        if (isSharingLocation.value) {
            mapViewModel.updateUserLocation(
                uniqueId = uniqueId.value,
                location = currentLocation.value,
                isSharingLocation = isSharingLocation.value
            )
        }
    }

    LaunchedEffect(key1 = groupId.value) {
        if (groupId.value == "") {
            mapViewModel.getUserLocationFromGroup(groupId.value)
        }
    }

    /*LaunchedEffect(key1 = currentLocation.value, key2 = groupId.value) {
        if (isSharingLocation.value) {
            if (groupId.value != "") {
                mapViewModel.updateUserLocationInGroup(
                    groupId = groupId.value,
                    userId = uniqueId.value,
                    username = username.value,
                    location = currentLocation.value,
                    isSharingLocation = isSharingLocation.value
                )
            }
        }
    }*/

    LaunchedEffect(key1 = isSharingLocation.value) {
        mapViewModel.sharingLocation(uniqueId.value, isSharingLocation.value)
    }

    LaunchedEffect(key1 = isSharingLocation.value) {
        if (groupId.value != "") {
            mapViewModel.updateUserSharingLocation(
                groupId = groupId.value,
                userId = uniqueId.value,
                isShare = isSharingLocation.value
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier
                        .statusBarsPadding()
                        .height(50.dp)
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(color = MaterialTheme.colorScheme.background.copy(alpha = 0.45f)),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                fontSize = 16.sp,
                                lineHeight = 20.sp,
                                text = if (groupName.value == "") "No Group Selected" else groupName.value
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                                contentDescription = "ArrowBack",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                            )
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                        ) {
                            IconButton(
                                onClick = {
                                    if (isLocationEnabled(mContext = context)) {
                                        updateCameraPosition(currentLocation.value)
                                    } else {
                                        showDialog.value = !isLocationEnabled(context)
                                    }
                                }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.my_location),
                                    contentDescription = "ArrowBack",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                                )
                            }
                            IconButton(
                                onClick = {
                                    showMapMenu.value = !showMapMenu.value
                                }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "ArrowBack",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->

            val showUserDetails = rememberSaveable { mutableStateOf(false) }
            val userSharingLocation = rememberSaveable {
                mutableStateOf(false)
            }


            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        updateCameraPosition(latLng)
                    },
                    onMapLoaded = {

                    },
                    properties = mapProperty.value,
                    uiSettings = mapUiSetting.value,
                    contentPadding = PaddingValues(top = 150.dp, bottom = 50.dp)
                ) {

                    Marker(
                        state = MarkerState(position = currentLocation.value),
                        title = "Your location",
                        onClick = {
                            return@Marker false
                        },
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE),
                    )


                    userLocationsList.value.forEach { location ->
                        if (uniqueId.value != location.userId) {

                            MarkerInfoWindow(
                                state = MarkerState(
                                    position = LatLng(
                                        location.latitude ?: 0.0,
                                        location.longitude ?: 0.0
                                    )
                                ),
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    if (location.isSharingLocation == true) BitmapDescriptorFactory.HUE_GREEN else BitmapDescriptorFactory.HUE_RED
                                ),
                                onClick = {
                                    showUserDetails.value = true
                                    userSharingLocation.value = location.isSharingLocation == true
                                    mapViewModel.getUserById(location.userId.toString())
                                    return@MarkerInfoWindow false
                                },
                            ) {
                                Column(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            MaterialTheme.colorScheme.background.copy(
                                                alpha = 0.9f
                                            )
                                        )
                                        .padding(horizontal = 5.dp)
                                ) {
                                    if (location.username?.isNotEmpty() == true) {
                                        Text(text = location.username.toString())
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .background(Color.Transparent)
                ) {

                    OnMapControls(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        userUniqueIdViewModel = userUniqueIdViewModel,
                        showMapMenu = showMapMenu,
                        onShareLocation = isSharingLocation
                    )

                    BottomSheetForMapType(
                        modifier = Modifier
                            .align(Alignment.BottomStart),
                        mapProperty = mapProperty,
                    )

                    if (showDialog.value) {
                        ShowDialogToShowLocationIsDisableOrNot(
                            showDialog = showDialog,
                        )
                    }

                    if (showUserDetails.value) {
                        ShowAboutUser(
                            modifier = Modifier
                                .statusBarsPadding()
                                .align(Alignment.TopCenter),
                            showUserDetails = showUserDetails,
                            user = userInfo,
                            userSharingLocation = userSharingLocation
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun OnMapControls(
    modifier: Modifier,
    userUniqueIdViewModel: UserUniqueIdViewModel,
    showMapMenu: MutableState<Boolean>,
    onShareLocation: State<Boolean>,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 70.dp, end = 10.dp, top = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AnimatedVisibility(
            visible = showMapMenu.value,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                .padding(horizontal = 8.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = "Share your location for others",
                    color = if (onShareLocation.value) Color.Green else MaterialTheme.colorScheme.onBackground
                )
                Switch(
                    checked = onShareLocation.value,
                    onCheckedChange = {
                        userUniqueIdViewModel.saveUserSharingLocation(it)
                    },
                    thumbContent = if (onShareLocation.value) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }
}

@Composable
private fun BottomSheetForMapType(
    mapProperty: MutableState<MapProperties>,
    modifier: Modifier
) {

    val showBottomSheet = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .padding(bottom = 30.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AnimatedVisibility(
            visible = showBottomSheet.value,
            modifier = Modifier
                .padding(start = 10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.45f))
        ) {
            Column {
                mapTypeData().forEach { it: Pair<MapType, String> ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .clickable {
                                    mapProperty.value = mapProperty.value.copy(mapType = it.first)
                                    showBottomSheet.value = !showBottomSheet.value
                                }
                                .padding(8.dp)
                                .width(80.dp),
                            text = it.second,
                            style = TextStyle(
                                color = if (it.first == mapProperty.value.mapType) Color.Green
                                else MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.8f
                                )
                            )
                        )
                    }
                }
            }
        }

        IconButton(
            modifier = Modifier
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.25f)),
            onClick = {
                showBottomSheet.value = !showBottomSheet.value
            }) {
            Icon(painter = painterResource(id = R.drawable.map), contentDescription = "map")
        }
    }
}

@Composable
fun mapTypeData(): List<Pair<MapType, String>> {
    return listOf(
        MapType.NORMAL to "ROADMAP",
        MapType.SATELLITE to "SATELLITE",
        MapType.HYBRID to "HYBRID",
        MapType.TERRAIN to "TERRAIN",
        //MapType.NONE to "NONE"
    )
}


@Composable
fun ShowDialogToShowLocationIsDisableOrNot(
    showDialog: MutableState<Boolean>,
) {

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(
                    text = "Location Services Disabled",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = "Please enable location services to use this feature.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Ok", color = MaterialTheme.colorScheme.background)
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.background)
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ShowAboutUser(
    modifier: Modifier = Modifier,
    showUserDetails: MutableState<Boolean>,
    user: State<DataOrException<UserInfo?, Boolean, Exception>>,
    userSharingLocation: MutableState<Boolean>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.72f))
            .padding(15.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        val imageUrl =
            if (user.value.data?.profilePic.isNullOrEmpty()) R.drawable.person else user.value.data?.profilePic

        AnimatedVisibility(user.value.loading == true) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(40.dp)
                    .padding(5.dp),
                strokeWidth = 2.dp
            )
        }

        AnimatedVisibility(visible = user.value.data != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {

                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            model = imageUrl,
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(25.dp)
                                        .padding(15.dp),
                                    strokeWidth = 1.5.dp
                                )
                            },
                            contentDescription = "profileImage"
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.is_location_on),
                            contentDescription = null,
                            tint = if (userSharingLocation.value) Color.Green else Color.Red
                        )
                    }
                    Icon(
                        modifier = Modifier.clickable {
                            showUserDetails.value = !showUserDetails.value
                        },
                        imageVector = Icons.Default.Close, contentDescription = null
                    )
                }
                Text(text = user.value.data?.username.toString())
                Text(text = user.value.data?.mail.toString())
            }
        }
    }
}
