package darkube.service.geolocation.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import org.json.JSONObject

object ServiceManager {

    private const val TAG = "service-manager"

    fun isRunning(): Boolean {
        return ForegroundLocationTracking.isRunning
    }

    fun start(context: Context, json: JSONObject) {
        if (!Permissions.hasLocation(context) || !Permissions.hasLocation(context)) {
            Log.d(TAG, "permissions denied to start the service")
            return
        }
        val interval = json.optLong("interval", 5000L)
        val distance = json.optDouble("distanceFilter", 5.0).toFloat()
        val intent = Intent(context, ForegroundLocationTracking::class.java).apply {
            action = ForegroundLocationTracking.ACTION_UPDATE
            putExtra("interval", interval)
            putExtra("distance", distance)
        }
        ContextCompat.startForegroundService(context, intent)
        Log.d(TAG, "service started")
    }

    fun stop(context: Context) {
        if (isRunning()) {
            val intent = Intent(context, ForegroundLocationTracking::class.java).apply {
                action = ForegroundLocationTracking.ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
        Log.d(TAG, "service stopped")
    }

    fun forceStop(context: Context) {
        if (isRunning()) {
            val intent = Intent(context, ForegroundLocationTracking::class.java)
            context.stopService(intent)
        }
        Log.d(TAG, "service force stopped")
    }

}
