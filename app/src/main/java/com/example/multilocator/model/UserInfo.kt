package com.example.multilocator.model

data class UserInfo(
    var mail: String? = null,
    var profilePic: String? = null,
    var uniqueId: String? = null,
    var username: String? = null,
){
    constructor() : this(null,  null, null, null)

    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "mail" to this.mail,
            "profilePic" to this.profilePic,
            "uniqueId" to this.uniqueId,
            "username" to this.username
        )
    }
}
