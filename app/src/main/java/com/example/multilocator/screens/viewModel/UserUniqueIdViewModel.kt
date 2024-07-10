package com.example.multilocator.screens.viewModel

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
    private val _uniqueId = MutableStateFlow<String>("")
    val uniqueId: StateFlow<String> = _uniqueId

    init {
        launchCatching {
            userUniqueIdRepository.getUserUniqueId().collect { uniqueId ->
                _uniqueId.value = uniqueId ?: ""
            }
        }
    }

    fun saveUniqueId(uniqueId: String) {
        launchCatching {
            userUniqueIdRepository.saveUserUniqueId(uniqueId)
        }
    }

    private val _username = MutableStateFlow<String>("")
    val username: StateFlow<String> = _username

    init {
        launchCatching {
            userUniqueIdRepository.getUserName().collect { username->
                _username.value = username ?: ""
            }
        }
    }

    fun saveUserName(username: String) {
        launchCatching {
            userUniqueIdRepository.saveUserName(username)
        }
    }
}