package io.homeassistant.companion.android

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.PowerManager
import dagger.hilt.android.HiltAndroidApp
import io.homeassistant.companion.android.sensors.SensorReceiver
import io.homeassistant.companion.android.websocket.WebsocketBroadcastReceiver
import com.joaomgcd.common.tasker.AutoAppsThirdParty
@HiltAndroidApp
open class HomeAssistantApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        AutoAppsThirdParty.authorize(context)
        
        registerReceiver(
            WebsocketBroadcastReceiver(),
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
            }
        )        
        
        val sensorReceiver = SensorReceiver()
        // This will cause the sensor to be updated every time the OS broadcasts that a cable was plugged/unplugged.
        // This should be nearly instantaneous allowing automations to fire immediately when a phone is plugged
        // in or unplugged. Updates will also be triggered when the system reports low battery and when it recovers.
        registerReceiver(
            sensorReceiver,
            IntentFilter().apply {
                addAction(Intent.ACTION_BATTERY_LOW)
                addAction(Intent.ACTION_BATTERY_OKAY)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
        )
    }
}
