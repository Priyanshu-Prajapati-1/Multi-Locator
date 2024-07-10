package com.example.multilocator.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.GroupInfo
import com.example.multilocator.model.User
import com.example.multilocator.model.UserInfo
import com.example.multilocator.model.UserLocation
import com.example.multilocator.navigation.MultiLocatorScreens
import com.example.multilocator.repository.ConnectivityRepository
import com.example.multilocator.repository.FireBaseRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val fireBaseRepository: FireBaseRepository,
    private val connectivityRepository: ConnectivityRepository,
    private val accountService: AccountService
) : MultiLocatorViewModel() {

    val userId = accountService.currentUserId

    val isOnline = connectivityRepository.isConnected

    val userGroups: StateFlow<DataOrException<List<String>?, Boolean, Exception>>
        get() = fireBaseRepository.userGroups

    val userGroupsInfo: StateFlow<DataOrException<List<GroupInfo>?, Boolean, Exception>>
        get() = fireBaseRepository.userGroupsInfo

    val user: StateFlow<DataOrException<User?, Boolean, Exception>>
        get() = fireBaseRepository.user

    val userInfo: StateFlow<DataOrException<UserInfo?, Boolean, Exception>>
        get() = fireBaseRepository.userInfo

    val getGroupUserWithName: StateFlow<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>
        get() = fireBaseRepository.getGroupUserWithName

    private val _userLocations = MutableStateFlow<Map<String, UserLocation>>(emptyMap())
    val userLocations: StateFlow<Map<String, UserLocation>> = _userLocations

    val isCreatingGroup: StateFlow<Boolean>
        get() = fireBaseRepository.isCreatedGroup

    var isUserAvailable: User by mutableStateOf(User())

    fun openSettings(openScreen: (String) -> Unit) {
        openScreen(MultiLocatorScreens.SettingsScreen.name)
    }

    fun createGroup(
        groupName: String, memberIds: List<String>,
        isCreatedGroup: (Boolean) -> Unit = {}
    ) {
        launchCatching {
            fireBaseRepository.createGroup(groupName, memberIds)
        }
    }

    fun getUserGroups(uniqueId: String) {
        launchCatching {
            fireBaseRepository.getUserGroups(uniqueId)
        }
    }

    fun getGroupInfo(groupIds: List<String>) {
        launchCatching {
            fireBaseRepository.getGroupInfo(groupIds)
        }
    }

    fun getUserById(userId: String) {
        launchCatching {
            fireBaseRepository.getUserById(userId)
        }
    }

    fun fetchUserLocation(userIds: List<String>) {
        launchCatching {
            fireBaseRepository.fetchUserLocationsInRealTime(userIds) { locations ->
                _userLocations.value = locations
            }
        }
    }

    fun getUserByUniqueId(uniqueId: String) {
        launchCatching {
            fireBaseRepository.getUserByUniqueId(uniqueId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        fireBaseRepository.removeLocationListeners()
    }

    fun addUserToGroup(groupId: String, userId: String) {
        launchCatching {
            fireBaseRepository.addUserToGroup(groupId, userId)
        }
    }

    fun getGroupMembersWithNames(groupId: String) {
        launchCatching {
            fireBaseRepository.getGroupMembersWithNames(groupId) {

            }
        }
    }
}