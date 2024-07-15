package com.example.multilocator.repository

import android.net.Uri
import android.util.Log
import com.example.multilocator.model.DataOrException
import com.example.multilocator.model.GroupInfo
import com.example.multilocator.model.LocationUpdate
import com.example.multilocator.model.UpdateUserLocation
import com.example.multilocator.model.User
import com.example.multilocator.model.UserInfo
import com.example.multilocator.model.UserLocation
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class FireBaseRepository @Inject constructor() {

    val database = Firebase.database
    val storage = FirebaseStorage.getInstance()

    private var _userGroups = MutableStateFlow<DataOrException<List<String>?, Boolean, Exception>>(
        DataOrException(null, false, Exception(""))
    )
    val userGroups: StateFlow<DataOrException<List<String>?, Boolean, Exception>>
        get() = _userGroups

    private var _userGroupsInfo =
        MutableStateFlow<DataOrException<List<GroupInfo>?, Boolean, Exception>>(
            DataOrException(null, false, Exception(""))
        )
    val userGroupsInfo: StateFlow<DataOrException<List<GroupInfo>?, Boolean, Exception>>
        get() = _userGroupsInfo

    private var _user =
        MutableStateFlow<DataOrException<User?, Boolean, Exception>>(
            DataOrException(null, false, Exception(""))
        )
    val user: StateFlow<DataOrException<User?, Boolean, Exception>>
        get() = _user

    private var _isCreatedGroup = MutableStateFlow(false)
    val isCreatedGroup: StateFlow<Boolean>
        get() = _isCreatedGroup

    private var _getGroupUserWithName =
        MutableStateFlow<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>(
            DataOrException(null, false, Exception(""))
        )
    val getGroupUserWithName: StateFlow<DataOrException<Map<String, UserInfo>?, Boolean, Exception>>
        get() = _getGroupUserWithName


    private var _userLocationList =
        MutableStateFlow<DataOrException<List<UserLocation>?, Boolean, Exception>>(
            DataOrException(null, false, Exception(""))
        )
    val userLocationList: StateFlow<DataOrException<List<UserLocation>?, Boolean, Exception>>
        get() = _userLocationList

    suspend fun addUserToDatabase(
        password: String,
        user: UserInfo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        database.reference.child("UserInfo").child(user.uniqueId.toString())
            .setValue(user.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database.reference.child("UsersAccount")
                        .child(FirebaseAuth.getInstance().uid.toString())
                        .setValue(
                            User(
                                mail = user.mail,
                                password = password,
                                profilePic = user.profilePic,
                                userId = FirebaseAuth.getInstance().uid,
                                userUniqueId = user.uniqueId,
                                username = user.username
                            ).toMap()
                        ).addOnCompleteListener {
                            onSuccess()
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

    }

    suspend fun createGroup(
        groupName: String, memberIds: List<String>,
        isCreatedGroup: (Boolean) -> Unit = {}
    ) {
        val groupRef = database.getReference("groups").push()
        val groupId = groupRef.key ?: return

        val groupData = mapOf("name" to groupName)

        _isCreatedGroup.value = true
        groupRef.setValue(groupData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val groupMembersRef = database.getReference("groups/$groupId/members")
                val userGroupUpdates = mutableMapOf<String, Any>()

                memberIds.forEach { userId ->
                    groupMembersRef.child(userId).setValue(true)
                    userGroupUpdates["users/$userId/groups/$groupId"] = true
                }
                database.reference.updateChildren(userGroupUpdates)
                    .addOnCompleteListener { userGroupsTask ->
                        if (userGroupsTask.isSuccessful) {
                            _isCreatedGroup.value = false
                        } else {
                            println("Failed to update user's groups: ${userGroupsTask.exception?.message}")
                        }
                    }
            } else {
                println("Failed to create group: ${task.exception?.message}")
                _isCreatedGroup.value = false
            }
        }
    }

    suspend fun updateMapLocation(userId: String, location: LatLng, isSharingLocation: Boolean) {
        database.getReference("users/${FirebaseAuth.getInstance().uid}/location").setValue(location)
    }

    suspend fun userSharingLocation(uniqueId: String, isShare: Boolean) {
        database.getReference("users/${FirebaseAuth.getInstance().uid}/sharingLocation")
            .setValue(isShare)
    }

    suspend fun getUserGroups(userId: String) {
        val databaseReference = database.getReference("users/$userId/groups")

        _userGroups.value = DataOrException(null, true, null)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupIds = mutableListOf<String>()
                for (groupSnapShot in snapshot.children) {
                    groupSnapShot.key?.let {
                        groupIds.add(it)
                    }
                }
                _userGroups.value = DataOrException(groupIds, false, null)
            }

            override fun onCancelled(error: DatabaseError) {
                _userGroups.value = DataOrException(null, false, error.toException())
            }
        })
    }

    suspend fun getGroupInfo(groupIds: List<String>) {
        val databaseReference = database.getReference("groups")
        val groupInfoList = mutableListOf<GroupInfo>()

        if (groupIds.isEmpty()) {
            _userGroupsInfo.value = DataOrException(null, false, null)
            return
        }

        _userGroupsInfo.value = DataOrException(null, true, null)
        for (groupId in groupIds) {
            databaseReference.child(groupId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val groupName = snapshot.child("name").getValue(String::class.java)
                        val groupInfo = GroupInfo(name = groupName, id = groupId)
                        groupInfoList.add(groupInfo)
                        if (groupInfoList.size == groupIds.size) {
                            _userGroupsInfo.value = DataOrException(groupInfoList, false, null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _userGroupsInfo.value = DataOrException(null, false, error.toException())
                    }
                })
        }
    }

    fun getGroupMembersWithNames(
        groupId: String,
        onMembersReceived: (Map<String, String>) -> Unit
    ) {
        val groupMembersRef = database.getReference("groups/$groupId/members")
        val usersAccountRef = database.getReference("UserInfo")
        val membersMap = mutableMapOf<String, UserInfo>()

        _getGroupUserWithName.value = DataOrException(null, true, Exception(""))
        groupMembersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                membersMap.clear()
                val memberIds = snapshot.children.mapNotNull { child ->
                    child.key.takeIf { child.getValue(Boolean::class.java) == true }
                }

                if (memberIds.isNotEmpty()) {
                    usersAccountRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(userSnapshot: DataSnapshot) {
                            memberIds.forEach { memberId ->
                                val user = userSnapshot.child(memberId)
                                    .getValue(UserInfo::class.java) ?: UserInfo()
                                membersMap[memberId] = user
                            }
                            _getGroupUserWithName.value =
                                DataOrException(membersMap, false, Exception(""))
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle possible errors.
                            Log.e("FirebaseError", "Error fetching users: ${error.message}")
                            _getGroupUserWithName.value =
                                DataOrException(null, false, error.toException())
                        }
                    })
                } else {
                    _getGroupUserWithName.value = DataOrException(membersMap, false, null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors.
                _getGroupUserWithName.value.loading = false
                Log.e("FirebaseError", "Error fetching members: ${error.message}")
            }
        })
    }

    suspend fun getUserById(userId: String) {
        val databaseReference = database.getReference("UsersAccount/$userId")

        _user.value = DataOrException(null, true, Exception(""))
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("snapshot", "${snapshot.value}")
                val user = snapshot.getValue(User::class.java) ?: User()
                _user.value = DataOrException(
                    User(
                        mail = user.mail,
                        password = user.password,
                        profilePic = user.profilePic,
                        userId = user.userId,
                        username = user.username
                    ), false, null
                )
            }

            override fun onCancelled(error: DatabaseError) {
                _user.value = DataOrException(null, false, error.toException())
            }
        })
    }

    private val locationListeners = mutableMapOf<String, ValueEventListener>()
    suspend fun fetchUserLocationsInRealTime(
        userIds: List<String>,
        onDataChange: (Map<String, UserLocation>) -> Unit
    ) {
        val userLocations = mutableMapOf<String, UserLocation>()

        userIds.forEach { userId ->
            val userRef = database.getReference("userLocation/$userId")

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val latitude = snapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = snapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                    val isSharingLocation =
                        snapshot.child("isSharingLocation").getValue(Boolean::class.java) ?: false
                    userLocations[userId] = UserLocation(
                        userId,
                        LatLng(latitude, longitude),
                        isSharing = isSharingLocation
                    )
                    onDataChange(userLocations)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FireBaseRepository", "Error fetching user locations: ${error.message}")
                }
            }
            locationListeners[userId] = listener
            userRef.addValueEventListener(listener)
        }
    }

    fun removeLocationListeners() {
        locationListeners.forEach { (userId, listener) ->
            database.getReference("locations/$userId").removeEventListener(listener)
        }
        locationListeners.clear()
    }

    suspend fun addUserToGroup(groupId: String, userId: String) {
        val groupMemberRef = database.getReference("groups/$groupId/members/$userId")
        groupMemberRef.setValue(true).addOnCompleteListener {
            if (it.isSuccessful) {
                val userGroupRef = database.getReference("users/$userId/groups/$groupId")
                userGroupRef.setValue(true)
            }
        }
    }

    private val _isImageUpdate = MutableStateFlow(false)
    val isImageUpdate: StateFlow<Boolean>
        get() = _isImageUpdate

    suspend fun updateImage(userUniqueId: String, uri: Uri?, isUpdateComplete: (Boolean) -> Unit) {
        val reference = storage.reference.child("profilePic")
            .child(FirebaseAuth.getInstance().uid!!)

        _isImageUpdate.value = true
        reference.putFile(uri!!)
            .addOnSuccessListener {
                reference.downloadUrl
                    .addOnSuccessListener { _uri ->
                        database.reference
                            .child("UserInfo")
                            .child(userUniqueId)
                            .child("profilePic")
                            .setValue(_uri.toString())
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    database.reference
                                        .child("UsersAccount")
                                        .child(FirebaseAuth.getInstance().uid!!)
                                        .child("profilePic")
                                        .setValue(_uri.toString())
                                        .addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                _isImageUpdate.value = false
                                                isUpdateComplete(true)
                                            }
                                        }
                                }
                            }
                    }
            }
    }


    private var _userInfo =
        MutableStateFlow<DataOrException<UserInfo?, Boolean, Exception>>(
            DataOrException(null, false, Exception(""))
        )
    val userInfo: StateFlow<DataOrException<UserInfo?, Boolean, Exception>>
        get() = _userInfo

    suspend fun getUserByUniqueId(uniqueId: String) {
        val databaseReference = database.getReference("UserInfo/$uniqueId")

        _userInfo.value = DataOrException(null, true, Exception(""))
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserInfo::class.java) ?: UserInfo()
                _userInfo.value = DataOrException(
                    UserInfo(
                        mail = user.mail,
                        profilePic = user.profilePic,
                        uniqueId = user.uniqueId,
                        username = user.username
                    ), false, null
                )
            }

            override fun onCancelled(error: DatabaseError) {
                _userInfo.value = DataOrException(null, false, error.toException())
            }
        })
    }

    suspend fun getUserUniqueId(
        onReceiveUniqueId: (String, String) -> Unit
    ) {
        val reference =
            database.reference.child("UsersAccount/${FirebaseAuth.getInstance().uid}")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val id = snapshot.child("userUniqueId").getValue(String::class.java) ?: ""
                val name = snapshot.child("username").getValue(String::class.java) ?: ""
                onReceiveUniqueId(id, name)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    suspend fun updateUserLocationInGroup(
        groupId: String,
        userId: String,
        userName: String,
        location: LatLng,
        isSharingLocation: Boolean
    ) {
        val reference = database.reference.child("groupLocation/$groupId/$userId")
        reference.setValue(
            UpdateUserLocation(
                userId = userId,
                username = userName,
                latitude = location.latitude,
                longitude = location.longitude,
                isSharingLocation = isSharingLocation
            ).toMap()
        )
    }

    suspend fun updateUserSharingLocation(groupId: String, userId: String, isShare: Boolean) {
        val reference = database.reference.child("groupLocation/$groupId/$userId/isSharingLocation")
        reference.setValue(isShare)
    }

    suspend fun getUserLocationFromGroup(
        groupId: String,
        onResult: (List<UpdateUserLocation>) -> Unit
    ) {
        val reference = database.reference.child("groupLocation/$groupId")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userLocations = mutableListOf<UpdateUserLocation>()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.child("userId").getValue(String::class.java)
                    val username = userSnapshot.child("username").getValue(String::class.java)
                    val latitude = userSnapshot.child("latitude").getValue(Double::class.java)
                    val longitude = userSnapshot.child("longitude").getValue(Double::class.java)
                    val isSharingLocation =
                        userSnapshot.child("isSharingLocation").getValue(Boolean::class.java)

                    userLocations.add(
                        UpdateUserLocation(
                            userId = userId,
                            username = username,
                            latitude = latitude,
                            longitude = longitude,
                            isSharingLocation = isSharingLocation
                        )
                    )
                }
                onResult(userLocations)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}