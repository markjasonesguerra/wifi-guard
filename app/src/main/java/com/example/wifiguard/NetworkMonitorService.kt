package com.example.wifiguard

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

class NetworkMonitorService : Service() {

    private var job: Job? = null
    private var lastState: String? = null

    override fun onCreate() {
        super.onCreate()
        createChannels()
        startForeground(1, buildForegroundNotification("Monitoring network..."))
        startMonitoring()
    }

    private fun startMonitoring() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val isWifi = isWifiConnected()
                val hasInternet = if (isWifi) checkInternet() else false

                // Determine the exact string state
                val currentState = when {
                    isWifi && hasInternet -> "Connected"
                    isWifi && !hasInternet -> "No Internet"
                    else -> "Disconnected"
                }

                // Only update if the state actually changed
                if (lastState == null || lastState != currentState) {
                    lastState = currentState

                    // In startMonitoring() inside the Service
                    LogStore.add(this@NetworkMonitorService, currentState)

                    // Send update to MainActivity
                    val intent = Intent("com.example.wifiguard.NETWORK_STATUS_UPDATE")
                    intent.setPackage(packageName)
                    intent.putExtra("online", hasInternet)
                    intent.putExtra("wifiConnected", isWifi)
                    sendBroadcast(intent)
                }

                // Faster check: 2 seconds instead of 5
                delay(2000)
            }
        }
    }

    private fun isWifiConnected(): Boolean {
        return try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } catch (e: Exception) { false }
    }

    private fun checkInternet(): Boolean {
        return try {
            val url = URL("https://clients3.google.com/generate_204")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 1500
            conn.readTimeout = 1500
            conn.connect()
            conn.responseCode == 204
        } catch (e: Exception) {
            try {
                Socket().use { it.connect(InetSocketAddress("1.1.1.1", 443), 1500) }
                true
            } catch (e2: Exception) { false }
        }
    }

    private fun buildForegroundNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "wifi_guard")
            .setContentTitle("WiFi Guard")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(NotificationChannel("wifi_guard", "WiFi Guard", NotificationManager.IMPORTANCE_LOW))
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onDestroy() { job?.cancel(); super.onDestroy() }
    override fun onBind(intent: Intent?): IBinder? = null
}