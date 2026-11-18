package com.example.omniclock.data.model

import java.time.LocalTime
import java.util.UUID

data class Alarm(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean = true,
    val label: String = "",
    val eventType: EventType = EventType.GENERAL,
    val recurrence: RecurrenceRule = RecurrenceRule(),
    val isVibrate: Boolean = true,
    val soundTitle: String = "Default Alarm Tone",
    val snoozeMinutesRemaining: Int? = null
) {
    val formattedTime: String
        get() = LocalTime.of(hour, minute).let {
            val h = if (it.hour % 12 == 0) 12 else it.hour % 12
            val amPm = if (it.hour < 12) "AM" else "PM"
            String.format("%02d:%02d %s", h, it.minute, amPm)
        }

    val displayTitle: String
        get() = if (label.isNotBlank()) "${eventType.emoji} $label" else "${eventType.emoji} ${eventType.title}"
}
