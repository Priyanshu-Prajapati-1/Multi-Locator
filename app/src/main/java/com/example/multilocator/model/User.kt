package com.example.multilocator.model

import androidx.compose.runtime.Immutable
import com.google.firebase.database.IgnoreExtraProperties

@Immutable
@IgnoreExtraProperties
data class User(
    var mail: String? = null,
    var password: String? = null,
    var profilePic: String? = null,
    var userId: String? = null,
    var userUniqueId: String? = null,
    var username: String? = null,
) {

    constructor() : this(null, null, null, null, null)

    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "mail" to this.mail,
            "password" to this.password,
            "profilePic" to this.profilePic,
            "userId" to this.userId,
            "userUniqueId" to this.userUniqueId,
            "username" to this.username
        )
    }
}