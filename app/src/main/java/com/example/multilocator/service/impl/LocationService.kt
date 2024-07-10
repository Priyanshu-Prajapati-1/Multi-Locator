package com.example.multilocator.service.impl

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.multilocator.service.ILocationService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationService @Inject constructor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) : ILocationService {

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun requestLocationUpdates(): Flow<LatLng?> = callbackFlow {
        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.locations.lastOrNull()?.let {
                    trySend(LatLng(it.latitude, it.longitude))
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun requestCurrentLocation(): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    companion object {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1500)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()
    }
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

