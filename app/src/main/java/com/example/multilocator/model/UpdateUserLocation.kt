package com.example.multilocator.model

import androidx.compose.runtime.Immutable
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UpdateUserLocation(
    val userId: String? = null,
    val username : String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isSharingLocation: Boolean? = null
) {
    constructor() : this(null, null, null,null, null)

    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "userId" to this.userId,
            "username" to this.username,
            "latitude" to this.latitude,
            "longitude" to this.longitude,
            "isSharingLocation" to this.isSharingLocation
        )
    }
}
