package com.example.multilocator.components.utils

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

fun isLocationEnabled(mContext: Context): Boolean {
    val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

fun generateUniqueID(): String {
    return UUID.randomUUID().toString()
}

fun parseLatLong(latLong: String): LatLng {
    val parts = latLong.split(",")

    if (parts.size != 2) {
        return LatLng(0.0, 0.0)
    }
    val lat = parts[0].trim().toDoubleOrNull()
    val lng = parts[1].trim().toDoubleOrNull()

    if (lat == null || lng == null) {
        return LatLng(0.0, 0.0)
    }
    return LatLng(lat, lng)
}