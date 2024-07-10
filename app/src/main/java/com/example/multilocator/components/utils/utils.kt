package com.example.multilocator.components.utils

import android.content.Context
import android.location.LocationManager
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