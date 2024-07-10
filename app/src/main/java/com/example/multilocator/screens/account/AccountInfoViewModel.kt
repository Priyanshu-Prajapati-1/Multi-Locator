package com.example.multilocator.screens.account

import android.net.Uri
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.UserInfo
import com.example.multilocator.repository.FireBaseRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.example.multilocator.service.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val fireBaseRepository: FireBaseRepository,
    private val accountService: AccountService,
) : MultiLocatorViewModel() {

    val userInfo: StateFlow<DataOrException<UserInfo?, Boolean, Exception>>
        get() = fireBaseRepository.userInfo

    val isImageUpdate: StateFlow<Boolean>
        get() = fireBaseRepository.isImageUpdate

    fun getUserByUniqueId(uniqueId: String) {
        launchCatching {
            fireBaseRepository.getUserByUniqueId(uniqueId)
        }
    }

    fun updateImage(userUniqueId: String, uri: Uri?, isUpdateComplete: (Boolean) -> Unit) {
        launchCatching {
            fireBaseRepository.updateImage(
                userUniqueId,
                uri
            ) { isUpdateComplete ->
                isUpdateComplete(isUpdateComplete)
            }
        }
    }
}