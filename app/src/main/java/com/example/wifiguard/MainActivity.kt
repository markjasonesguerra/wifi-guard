package com.example.wifiguard

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    enum class ConnectionState {
        CONNECTED, NO_INTERNET, DISCONNECTED
    }

    private var statusText: TextView? = null
    private var wifiIcon: ImageView? = null
    private var statusCircle: View? = null
    private var pulse1: View? = null
    private var pulse2: View? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            startNetworkService()
        }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            try {
                val online = intent?.getBooleanExtra("online", false) ?: false
                val wifiConnected = intent?.getBooleanExtra("wifiConnected", false) ?: false

                val state = when {
                    wifiConnected && online -> ConnectionState.CONNECTED
                    wifiConnected && !online -> ConnectionState.NO_INTERNET
                    else -> ConnectionState.DISCONNECTED
                }
                updateStatus(state)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogStore.init(this)
        try {
            setContentView(R.layout.activity_main)

            statusText = findViewById(R.id.statusText)
            wifiIcon = findViewById(R.id.wifiIcon)
            statusCircle = findViewById(R.id.statusCircle)
            pulse1 = findViewById(R.id.radarPulse1)
            pulse2 = findViewById(R.id.radarPulse2)

            // Start animations with the 1250ms spacing for perfect smoothness
            startPulseAnimations()

            findViewById<View>(R.id.openLogsBtn)?.setOnClickListener {
                startActivity(Intent(this, LogsActivity::class.java))
            }

            requestPermissionAndStartService()
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun startPulseAnimations() {
        try {
            val anim1 = AnimationUtils.loadAnimation(this, R.anim.radar_pulse)
            val anim2 = AnimationUtils.loadAnimation(this, R.anim.radar_pulse)

            pulse1?.startAnimation(anim1)
            pulse2?.postDelayed({
                if (!isFinishing) pulse2?.startAnimation(anim2)
            }, 1250)
        } catch (e: Exception) {}
    }

    private fun updateStatus(state: ConnectionState) {
        try {
            when (state) {
                ConnectionState.CONNECTED -> {
                    statusText?.text = "Connected"
                    wifiIcon?.setImageResource(R.drawable.ic_connected)
                    wifiIcon?.setColorFilter(Color.parseColor("#00BC7D"))

                    statusCircle?.setBackgroundResource(R.drawable.bg_status_circle)
                    pulse1?.setBackgroundResource(R.drawable.bg_radar_pulse)
                    pulse2?.setBackgroundResource(R.drawable.bg_radar_pulse)
                }
                ConnectionState.NO_INTERNET -> {
                    statusText?.text = "No Internet"
                    wifiIcon?.setImageResource(R.drawable.ic_no_internet)
                    wifiIcon?.setColorFilter(Color.parseColor("#F59E0B"))

                    statusCircle?.setBackgroundResource(R.drawable.bg_status_circle_warning)
                    pulse1?.setBackgroundResource(R.drawable.bg_radar_pulse_warning)
                    pulse2?.setBackgroundResource(R.drawable.bg_radar_pulse_warning)
                }
                ConnectionState.DISCONNECTED -> {
                    statusText?.text = "Wi-Fi Disconnected"
                    wifiIcon?.setImageResource(R.drawable.ic_disconnected)
                    wifiIcon?.setColorFilter(Color.parseColor("#EF4444"))

                    statusCircle?.setBackgroundResource(R.drawable.bg_status_circle_error)
                    pulse1?.setBackgroundResource(R.drawable.bg_radar_pulse_error)
                    pulse2?.setBackgroundResource(R.drawable.bg_radar_pulse_error)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.example.wifiguard.NETWORK_STATUS_UPDATE")
        ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        try { unregisterReceiver(receiver) } catch (e: Exception) {}
    }

    private fun requestPermissionAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                startNetworkService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startNetworkService()
        }
    }

    private fun startNetworkService() {
        val serviceIntent = Intent(this, NetworkMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(serviceIntent)
        else startService(serviceIntent)
    }
}