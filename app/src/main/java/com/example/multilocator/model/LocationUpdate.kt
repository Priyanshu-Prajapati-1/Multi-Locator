package com.example.multilocator.model

import androidx.compose.runtime.Immutable
import com.google.firebase.database.IgnoreExtraProperties

@Immutable
@IgnoreExtraProperties
data class LocationUpdate(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isSharingLocation: Boolean? = null
) {
    constructor() : this(null,  null, null)

    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "latitude" to this.latitude,
            "longitude" to this.longitude,
            "isSharingLocation" to this.isSharingLocation
        )
    }
}