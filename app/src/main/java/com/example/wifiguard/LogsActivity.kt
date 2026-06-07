package com.example.wifiguard

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class LogsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_logs)

            // 1. BACK BUTTON
            findViewById<android.widget.ImageButton>(R.id.backBtn)?.setOnClickListener { finish() }

            val container = findViewById<LinearLayout>(R.id.logsContainer)

            // 2. CLEAR BUTTON LOGIC
            val clearBtn = findViewById<TextView>(R.id.clearLogsBtn)
            clearBtn.setOnClickListener {
                android.app.AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setTitle("Clear Logs?")
                    .setMessage("Delete all saved history permanently?")
                    .setPositiveButton("Delete") { _, _ ->
                        LogStore.clear(this) // Wipes the file
                        container?.removeAllViews() // Wipes the screen

                        // Show "Empty" message
                        val emptyText = TextView(this).apply {
                            text = "Logs cleared."
                            setTextColor(Color.parseColor("#88FFFFFF"))
                            textSize = 14f
                            setPadding(0, 50, 0, 0)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                        container?.addView(emptyText)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            // 3. DISPLAY THE LOGS (Your existing grouping code)
            val rawLogs = LogStore.getLogs()
            val groupedLogs = rawLogs.groupBy { it.split("|")[0] }

            val density = resources.displayMetrics.density
            val height64 = (64 * density).toInt()
            val margin12 = (12 * density).toInt()
            val padding16 = (16 * density).toInt()

            for ((date, logsForDay) in groupedLogs) {
                // Add Date Header
                val dateHeader = TextView(this).apply {
                    text = date
                    setTextColor(Color.parseColor("#94A3B8"))
                    textSize = 14f
                    setPadding(0, 24, 0, 16)
                    typeface = Typeface.DEFAULT_BOLD
                }
                container.addView(dateHeader)

                // Add Boxes
                for (entry in logsForDay.reversed()) {
                    val parts = entry.split("|")
                    if (parts.size < 3) continue

                    val time = parts[1]
                    val message = parts[2]

                    val logView = TextView(this).apply {
                        text = "$time  •  $message"
                        textSize = 12f
                        typeface = Typeface.MONOSPACE
                        setPadding(padding16, 0, padding16, 0)
                        gravity = Gravity.CENTER_VERTICAL

                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height64)
                        params.setMargins(0, 0, 0, margin12)
                        layoutParams = params

                        when {
                            message.contains("Disconnected") -> {
                                setTextColor(Color.parseColor("#EF4444"))
                                setBackgroundResource(R.drawable.bg_log_item_error)
                                setupIcon(this, R.drawable.ic_disconnected, "#EF4444")
                            }
                            message.contains("No Internet") -> {
                                setTextColor(Color.parseColor("#F59E0B"))
                                setBackgroundResource(R.drawable.bg_log_item_warning)
                                setupIcon(this, R.drawable.ic_no_internet, "#F59E0B")
                            }
                            else -> {
                                setTextColor(Color.parseColor("#00BC7D"))
                                setBackgroundResource(R.drawable.bg_log_item)
                                setupIcon(this, R.drawable.ic_connected, "#00BC7D")
                            }
                        }
                        compoundDrawablePadding = (12 * density).toInt()
                    }
                    container.addView(logView)
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    private fun setupIcon(view: TextView, drawableId: Int, colorHex: String) {
        val icon = ContextCompat.getDrawable(this, drawableId)
        icon?.setTint(Color.parseColor(colorHex))
        view.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
    }
}