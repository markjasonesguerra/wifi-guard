package com.example.wifiguard

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object LogStore {
    private val logs = mutableListOf<String>()
    private const val FILE_NAME = "network_logs.txt"

    // Load logs from phone memory when the app starts
    fun init(context: Context) {
        try {
            val file = context.getFileStreamPath(FILE_NAME)
            if (file.exists()) {
                val loadedLogs = context.openFileInput(FILE_NAME).bufferedReader().readLines()
                logs.clear()
                logs.addAll(loadedLogs)
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun add(context: Context, message: String) {
        val date = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val fullEntry = "$date|$time|$message"

        logs.add(fullEntry)
        saveToPhone(context)
    }

    private fun saveToPhone(context: Context) {
        try {
            context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).use { output ->
                logs.forEach { output.write("$it\n".toByteArray()) }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun getLogs(): List<String> = logs

    fun clear(context: Context) {
        logs.clear()
        try {
            // This deletes the physical file from the phone's storage
            context.deleteFile(FILE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}