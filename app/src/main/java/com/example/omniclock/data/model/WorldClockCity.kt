package com.example.omniclock.data.model

import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

data class WorldClockCity(
    val id: String,
    val cityName: String,
    val countryName: String,
    val flagEmoji: String,
    val zoneId: ZoneId,
    val isPinned: Boolean = false
) {
    val currentZonedDateTime: ZonedDateTime
        get() = ZonedDateTime.now(zoneId)

    val formattedTime: String
        get() = currentZonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))

    val formattedDate: String
        get() = currentZonedDateTime.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

    fun timeDifferenceFromLocal(): String {
        val localOffset = ZonedDateTime.now().offset.totalSeconds
        val targetOffset = currentZonedDateTime.offset.totalSeconds
        val diffHours = (targetOffset - localOffset) / 3600.0

        return when {
            diffHours == 0.0 -> "Same as local time"
            diffHours > 0 -> {
                val sign = "+"
                val hrs = diffHours.toInt()
                val mins = ((diffHours - hrs) * 60).toInt()
                if (mins == 0) "$sign$hrs hrs" else "$sign$hrs hrs $mins mins"
            }
            else -> {
                val hrs = diffHours.toInt()
                val mins = ((-diffHours + hrs) * 60).toInt()
                if (mins == 0) "$hrs hrs" else "$hrs hrs $mins mins"
            }
        }
    }
}
