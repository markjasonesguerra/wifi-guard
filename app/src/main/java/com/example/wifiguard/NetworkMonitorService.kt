package com.example.wifiguard

import android.app.*
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

class NetworkMonitorService : Service() {

    private var job: Job? = null
    private var lastOnlineState: Boolean? = null   // 👈 track last state

    override fun onCreate() {
        super.onCreate()
        createChannel()
        startForeground(1, buildNotification("Monitoring network..."))
        startMonitoring()
    }

    private fun startMonitoring() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val hasInternet = checkInternet()

                // 🔁 Only broadcast when state changes
                if (lastOnlineState == null || lastOnlineState != hasInternet) {
                    lastOnlineState = hasInternet

                    val intent = Intent("com.example.wifiguard.NETWORK_STATUS_UPDATE")
                    intent.putExtra("online", hasInternet)
                    sendBroadcast(intent)

                    if (!hasInternet) alertUser()
                }

                delay(5000) // still check every 5s, but no spam logs
            }
        }
    }

    private fun checkInternet(): Boolean {
        return try {
            val url = URL("https://clients3.google.com/generate_204")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            conn.connect()
            conn.responseCode == 204
        } catch (e: Exception) {
            false
        }
    }

    private fun alertUser() {
        val vibrator = getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(800, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(800)
        }

        val notif = buildNotification("⚠️ Internet Lost!")
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(2, notif)
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(this, "wifi_guard")
            .setContentTitle("WiFi Guard")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "wifi_guard",
                "WiFi Guard",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null
}