package com.example.multilocator.model

import com.google.android.gms.maps.model.LatLng


data class UserLocation(
    val userId: String? = null,
    val location: LatLng? = null,
    val isSharing: Boolean? = null
)