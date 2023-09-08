package com.example.weatherproject.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.recyclerview.widget.RecyclerView
import java.util.Date

fun RecyclerView.setupRecyclerView(layoutManager: RecyclerView.LayoutManager, adapter : RecyclerView.Adapter<*> ){
    this.layoutManager = layoutManager
    this.setHasFixedSize(true)
    this.isNestedScrollingEnabled = false
    this.adapter = adapter

}

fun Int.formatDate(format: String, offsetSeconds: Int): String {
    var timezone = "Unknown Timezone"
    val timezoneIds = TimeZone.getAvailableIDs(offsetSeconds * 1000)
    if (timezoneIds.isNotEmpty()) {
        val firstTimezoneId = timezoneIds[0]
        timezone = firstTimezoneId
    }
    val unixTimestamp = this
    val customDateFormat = SimpleDateFormat(format)
    customDateFormat.timeZone = android.icu.util.TimeZone.getTimeZone(timezone)
    val timestampMillis = unixTimestamp.toLong() * 1000
    val date = Date(timestampMillis)
    return customDateFormat.format(date)
}