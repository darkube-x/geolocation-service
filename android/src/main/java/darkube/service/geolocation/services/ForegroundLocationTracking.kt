package darkube.service.geolocation.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import darkube.service.geolocation.GeolocationServiceModule
import darkube.service.geolocation.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ForegroundLocationTracking : Service() {

    companion object {
        var isRunning = false
        const val CHANNEL_ID = "location_channel"
        const val PACKAGE = "darkube.service.geolocation"
        const val ACTION_STOP_SERVICE = "$PACKAGE.STOP_SERVICE"
        const val ACTION_UPDATE = "$PACKAGE.UPDATE"
    }

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var timeInterval = 5000L
    private var minimumDistance = 0f

    override fun onCreate() {
        super.onCreate()
        if (!Permissions.hasNotification(this) || !Permissions.hasLocation(this)) {
            return
        }
        createNotificationChannel()
        startForeground(1, buildNotification(null))
        startLocationUpdate(
            interval = 5000L,
            distance = 5f,
        )
        isRunning = true
    }

    private fun startLocationUpdate(interval: Long = 5000L, distance: Float = 0f) {
        timeInterval = interval
        minimumDistance = distance

        stopLocationUpdate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    updateNotification(location)
                    emitLocation(location)
                }
            }
        }
        val request = LocationRequest
            .Builder(interval)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setWaitForAccurateLocation(true)
            .build()

        try {
            fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (ex: SecurityException) {
            Log.e("LocationService", "Permission denied", ex)
        }
    }

    private fun stopLocationUpdate() {
        if (::fusedClient.isInitialized && ::locationCallback.isInitialized) {
            fusedClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun buildNotification(location: Location?): Notification {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent ?: Intent(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE
            else
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)

        val text = if (location != null) {
            "${location.latitude}\n${location.longitude}"
        } else {
            "....\n...."
        }
        remoteViews.setTextViewText(R.id.locationText, text)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSubText("location")
            .setSmallIcon(R.drawable.icon_map_point_rotate)
            .setCustomContentView(remoteViews)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSilent(true)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(location: Location) {
        if (!Permissions.hasNotification(this)) {
            return
        }
        val notification = buildNotification(location)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopSelf()
            return START_NOT_STICKY
        }
        if (intent?.action == ACTION_UPDATE) {
            val newInterval = intent.getLongExtra("interval", 5000L)
            val newDistance = intent.getFloatExtra("distance", 0f)
            startLocationUpdate(
                interval = newInterval,
                distance = newDistance,
            )
            return START_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdate()
        super.onDestroy()
        isRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun emitLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val json = mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "heading" to location.bearing,
                    "timestamp" to location.time,
                    "accuracy" to location.accuracy
                )
                val logText = "LOC :: ${location.latitude}, ${location.longitude}"
                Log.d("__loc",  logText)
                GeolocationServiceModule.emit(json)
            } catch (e: Exception) {
                Log.e("LocationService", "Failed to send location", e)
            }
        }
    }

}
