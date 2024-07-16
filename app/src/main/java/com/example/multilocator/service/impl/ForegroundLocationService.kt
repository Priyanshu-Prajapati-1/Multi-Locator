package com.example.multilocator.service.impl

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.compose.ui.util.trace
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.example.multilocator.MultiLocatorActivity
import com.example.multilocator.MultiLocatorHiltApp
import com.example.multilocator.R
import com.example.multilocator.model.UpdateUserLocation
import com.example.multilocator.repository.LocationRepository
import com.example.multilocator.repository.UserUniqueIdRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.database.database
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundLocationService : Service() {

    private val database = Firebase.database

    @Inject
    lateinit var repository: LocationRepository

    @Inject
    lateinit var userUniqueIdRepository: UserUniqueIdRepository

    private lateinit var notificationBuilder: NotificationCompat.Builder

    private var userId: String = ""
    private var groupId: String = ""
    private var groupName: String = ""
    private var userName: String = ""
    private var isSharingLocation: Boolean = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var location: Location? = null

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationCallback()
        initializeNotificationBuilder()

        CoroutineScope(Dispatchers.IO).launch {
            combine(
                userUniqueIdRepository.getUserUniqueId(),
                userUniqueIdRepository.getGroupId(),
                userUniqueIdRepository.getGroupName(),
                userUniqueIdRepository.getUserName(),
                userUniqueIdRepository.getUserSharingLocation(),
            ) { userUniqueId, group_Id, group_Name, username, isShare ->
                userId = userUniqueId ?: userId
                groupId = group_Id ?: groupId
                groupName = group_Name ?: groupName
                isSharingLocation = isShare ?: isSharingLocation
                userName = username ?: userName
                Log.d(
                    "userId, groupId, isSharingLocation, username",
                    "onCreate Service: $userId,  $groupId, $isSharingLocation, $userName"
                )
            }.collect {}
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        startForeground(NOTIFICATION_ID, getNotification())
        return START_STICKY
    }

    private fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { lastLocation ->
                    location = lastLocation
                    // Handle the new location
                    handleNewLocation(lastLocation)
                }
            }
        }
    }

    private fun handleNewLocation(location: Location) {
        if (::repository.isInitialized) {
            repository.updateLocation(location)
            updateNotification()
            if (isSharingLocation && groupId.isNotEmpty() && userId.isNotEmpty()) {
                updateUserLocationInGroup(
                    groupId = groupId,
                    userId = userId,
                    userName = userName,
                    location = LatLng(location.latitude, location.longitude),
                    isSharingLocation = isSharingLocation
                )
            } else {
                if (groupId.isNotEmpty()) {
                    updateUserSharingLocation(isSharingLocation)
                }
            }
        } else {
            Log.e("repo isInitialized", "Repository not initialized")
        }

        // 3. Log the new location (for debugging purposes)
        //Log.d("foreground location", "New location: ${location.latitude}, ${location.longitude}")
    }

    private fun updateUserSharingLocation(isShare: Boolean = false) {
        val reference = database.reference.child("groupLocation/$groupId/$userId")
        reference.child("isSharingLocation")
            .setValue(isShare)
    }

    /*private fun createNotification(): Notification {
        val notiIntent = Intent(this, MultiLocatorActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, MultiLocatorHiltApp.CHANNEL_ID)
            .setContentTitle("Location")
            .setContentText(if (isSharingLocation && groupId.isNotEmpty()) "Sharing Location for\nGroup : $groupName" else "Not Sharing Location for anyone")
            .setSmallIcon(R.drawable.directions_run)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setNotificationSilent()
            .setSound(null)

        return notification.build()
    }*/

    private fun initializeNotificationBuilder() {
        val notiIntent = Intent(this, MultiLocatorActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notiIntent, PendingIntent.FLAG_IMMUTABLE)

        notificationBuilder = NotificationCompat.Builder(this, MultiLocatorHiltApp.CHANNEL_ID)
            .setContentTitle("Location")
            .setSmallIcon(R.drawable.directions_run)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setNotificationSilent()
            .setSound(null)
    }

    private fun updateNotification() {
        val contentText = if (isSharingLocation && groupId.isNotEmpty()) {
            "Sharing Location for\nGroup : $groupName"
        } else if (groupId.isNotEmpty()) {
            "you track this group : $groupName"
        } else {
            ""
        }

        notificationBuilder.setContentText(contentText)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getNotification(): Notification {
        return notificationBuilder.build()
    }

    private fun requestLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1500)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(100)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private fun updateUserLocationInGroup(
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
        Log.d("update service", "location update")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Remove the notification
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)

        // Stop location updates
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    companion object {
        private const val NOTIFICATION_ID = 12345
    }
}