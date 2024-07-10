package com.example.multilocator.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

open class MultiLocatorViewModel : ViewModel() {
    fun launchCatching(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Log.d(ERROR_TAG, throwable.message.toString())
            },
            block = block
        )
    }

    companion object {
        const val ERROR_TAG = "MULTI LOCATOR APP ERROR"
    }
}