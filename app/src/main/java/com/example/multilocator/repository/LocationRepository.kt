package com.example.multilocator.repository

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.multilocator.service.impl.ForegroundLocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val context: Context
) {

    private val _locationFlow = MutableStateFlow<LatLng?>(null)
    val locationFlow: StateFlow<LatLng?> = _locationFlow

    @RequiresApi(Build.VERSION_CODES.O)
    fun startLocationUpdates() {
        val intent = Intent(context, ForegroundLocationService::class.java)
        context.startForegroundService(intent)
    }

    fun stopLocationUpdates() {
        context.stopService(Intent(context, ForegroundLocationService::class.java))
    }

    fun updateLocation(location: Location) {
        Log.d("LocationRepository", "Location: ${location.latitude}, ${location.longitude}")
        _locationFlow.value = LatLng(
            location.latitude,
            location.longitude
        )
    }
}