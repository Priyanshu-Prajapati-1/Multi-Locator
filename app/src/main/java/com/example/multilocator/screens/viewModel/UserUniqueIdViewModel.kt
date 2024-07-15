package com.example.multilocator.screens.viewModel

import android.util.Log
import com.example.multilocator.repository.UserUniqueIdRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserUniqueIdViewModel @Inject constructor(
    private val userUniqueIdRepository: UserUniqueIdRepository
) : MultiLocatorViewModel() {
    private val _uniqueId = MutableStateFlow("")
    val uniqueId: StateFlow<String> = _uniqueId

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _groupId = MutableStateFlow("")
    val groupId: StateFlow<String> = _groupId

    private val _groupName = MutableStateFlow("")
    val groupName: StateFlow<String> = _groupName

    private val _isSharingLocation = MutableStateFlow(false)
    val isSharingLocation: StateFlow<Boolean> = _isSharingLocation

    init {
        launchCatching {
            userUniqueIdRepository.getUserUniqueId().collect { uniqueId ->
                _uniqueId.value = uniqueId ?: ""
                userUniqueIdRepository.getUserName().collect { username ->
                    _username.value = username ?: ""
                    userUniqueIdRepository.getGroupId().collect { groupId ->
                        _groupId.value = groupId ?: ""
                        userUniqueIdRepository.getGroupName().collect { groupName ->
                            _groupName.value = groupName ?: ""
                            userUniqueIdRepository.getUserSharingLocation().collect { isShare ->
                                _isSharingLocation.value = isShare ?: false
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveUniqueId(uniqueId: String) {
        launchCatching {
            userUniqueIdRepository.saveUserUniqueId(uniqueId)
        }
    }

    fun saveUserName(username: String) {
        launchCatching {
            userUniqueIdRepository.saveUserName(username)
        }
    }

    fun saveGroupId(groupId: String) {
        launchCatching {
            userUniqueIdRepository.saveGroupId(groupId)
        }
    }

    fun saveGroupName(groupName: String) {
        launchCatching {
            userUniqueIdRepository.saveGroupName(groupName)
        }
    }

    fun saveUserSharingLocation(isShare: Boolean) {
        launchCatching {
            userUniqueIdRepository.saveUserSharingLocation(isShare)
        }
    }
}