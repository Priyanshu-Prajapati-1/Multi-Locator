package com.example.multilocator.screens.mapScreen

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.UpdateUserLocation
import com.example.multilocator.model.UserInfo
import com.example.multilocator.repository.FireBaseRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import com.example.multilocator.service.impl.GetLocationUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase,
    private val fireBaseRepository: FireBaseRepository,
    private val accountService: AccountService,
) : MultiLocatorViewModel() {

    val userId = accountService.currentUserId

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    val userInfo: StateFlow<DataOrException<UserInfo?, Boolean, Exception>>
        get() = fireBaseRepository.userInfo

    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String>
        get() = _groupName

    private val _selectedGroup = MutableStateFlow("")
    val selectedGroup: StateFlow<String>
        get() = _selectedGroup

    private val _groupId = MutableStateFlow("")
    val groupId: StateFlow<String>
        get() = _groupId

    private val _userLocations = MutableStateFlow<List<UpdateUserLocation>>(emptyList())
    val userLocations: StateFlow<List<UpdateUserLocation>>
        get() = _userLocations


    @RequiresApi(Build.VERSION_CODES.S)
    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                launchCatching {
                    getLocationUseCase.invoke().collect { location ->
                        _viewState.value = ViewState.Success(location)
                    }
                }
            }

            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }


    fun setGroupName(groupId: String, groupName: String) {
        _groupId.value = groupId
        _groupName.value = groupName
        _selectedGroup.value = groupName
    }

    fun updateUserLocation(uniqueId: String, location: LatLng, isSharingLocation: Boolean) {
        launchCatching {
            fireBaseRepository.updateMapLocation(
                userId = uniqueId,
                location = location,
                isSharingLocation = isSharingLocation
            )
        }
    }

    fun sharingLocation(uniqueId: String, isShare: Boolean = false) {
        launchCatching {
            fireBaseRepository.userSharingLocation(uniqueId = uniqueId, isShare = isShare)
        }
    }

    fun getUserById(userId: String) {
        launchCatching {
            fireBaseRepository.getUserByUniqueId(userId)
        }
    }

    fun updateUserSharingLocation(groupId: String, userId: String, isShare: Boolean) {
        launchCatching {
            fireBaseRepository.updateUserSharingLocation(
                groupId = groupId,
                userId = userId,
                isShare = isShare
            )
        }
    }

    fun updateUserLocationInGroup(
        groupId: String,
        userId: String,
        username: String,
        location: LatLng,
        isSharingLocation: Boolean
    ) {
        launchCatching {
            fireBaseRepository.updateUserLocationInGroup(
                groupId = groupId,
                userId = userId,
                userName = username,
                location = location,
                isSharingLocation = isSharingLocation
            )
        }
    }

    fun getUserLocationFromGroup(_groupId: String) {
        launchCatching {
            fireBaseRepository.getUserLocationFromGroup(
                groupId = _groupId
            ) { userLocationsList ->
                _userLocations.value = userLocationsList
            }
        }
    }
}

sealed interface ViewState {
    object Loading : ViewState
    data class Success(val location: LatLng?) : ViewState
    object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    object Granted : PermissionEvent
    object Revoked : PermissionEvent
}