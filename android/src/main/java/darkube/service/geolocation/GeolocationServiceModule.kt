package darkube.service.geolocation

import android.content.Intent
import android.content.Context
import androidx.core.os.bundleOf
import darkube.service.geolocation.services.ServiceManager
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL
import org.json.JSONObject

class GeolocationServiceModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("GeolocationService")
    Events("onComplete", "geoLocation")

    instance = this@GeolocationServiceModule

    Function("startService") { config: String ->
      val jsonConfig: JSONObject = try {
        JSONObject(config ?: "{}")
      } catch (e: Exception) {
        JSONObject()
      }
      ServiceManager.start(context, jsonConfig)
      this@GeolocationServiceModule.sendEvent("onComplete", bundleOf("status" to "started"))
    }

    Function("stopService") {
      ServiceManager.stop(context)
      this@GeolocationServiceModule.sendEvent("onComplete", bundleOf("status" to "stopped"))
    }

    Function("forceStopService") {
      ServiceManager.forceStop(context)
      this@GeolocationServiceModule.sendEvent("onComplete", bundleOf("status" to "force-stopped"))
    }

    Function("isServiceRunning") {
      return@Function ServiceManager.isRunning()
    }
  }


  private val context
    get() = requireNotNull(appContext.reactContext)

  companion object {
    private var instance: GeolocationServiceModule? = null

    fun emit(json: Map<String, Any>) {
      instance?.sendEvent("geoLocation", bundleOf("location" to json))
    }
  }

}
