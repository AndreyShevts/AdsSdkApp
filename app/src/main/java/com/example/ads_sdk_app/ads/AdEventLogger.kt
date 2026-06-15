package com.example.ads_sdk_app.ads

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdEventLogger(
    private val context: Context
) {

    private val logTag = "AdsDemo"
    private val logFileName = "appodeal_ad_events.log"

    fun log(event: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
        val line = "$time | $event\n"

        Log.d(logTag, event)

        try {
            File(context.filesDir, logFileName).appendText(line)
        } catch (e: Exception) {
            Log.e(logTag, "Failed to write ad event log", e)
        }
    }

    fun clear() {
        try {
            File(context.filesDir, logFileName).writeText("")
        } catch (e: Exception) {
            Log.e(logTag, "Failed to clear ad event log", e)
        }
    }
}