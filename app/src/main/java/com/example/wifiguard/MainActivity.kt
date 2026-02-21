package com.example.wifiguard

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val logs = StringBuilder()
        var uptime: Long = 0
        var downtime: Long = 0
        var lastStateTime: Long = System.currentTimeMillis()
        var lastOnline: Boolean = true
    }

    private lateinit var statusText: TextView
    private lateinit var logText: TextView
    private lateinit var statsText: TextView

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val online = intent?.getBooleanExtra("online", false) ?: false
            updateStatus(online)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        logText = findViewById(R.id.logText)
        statsText = findViewById(R.id.statsText)

        val serviceIntent = Intent(this, NetworkMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun onResume() {
        super.onResume()

        val filter = IntentFilter("com.example.wifiguard.NETWORK_STATUS_UPDATE")

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(receiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    private fun updateStatus(online: Boolean) {
        val now = System.currentTimeMillis()
        val diff = now - lastStateTime

        if (lastOnline) uptime += diff else downtime += diff
        lastStateTime = now
        lastOnline = online

        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        if (online) {
            statusText.text = "🟢 Internet Connected"
            logs.append("$time - Connected\n")
        } else {
            statusText.text = "🔴 No Internet"
            logs.append("$time - Disconnected / No Internet\n")
        }

        logText.text = logs.toString()
        statsText.text = "Uptime: ${uptime / 1000}s | Downtime: ${downtime / 1000}s"
    }
}