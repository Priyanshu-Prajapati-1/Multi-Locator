package com.example.multilocator.screens.viewModel

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.multilocator.repository.LocationRepository
import com.example.multilocator.screens.MultiLocatorViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: LocationRepository
) : MultiLocatorViewModel() {


    val location: StateFlow<LatLng?> = repository.locationFlow


    @RequiresApi(Build.VERSION_CODES.O)
    fun startLocationUpdates() {
        repository.startLocationUpdates()
    }

    fun stopLocationUpdates() {
        repository.stopLocationUpdates()
    }

}