package com.example.omniclock.data.model

enum class EventType(
    val title: String,
    val emoji: String,
    val defaultToneName: String
) {
    GENERAL("General", "⏰", "Digital Alarm"),
    BIRTHDAY("Birthday", "🎂", "Celebration Chime"),
    ANNIVERSARY("Anniversary", "💍", "Romantic Harp"),
    MEETING("Meeting", "📅", "Briefing Alert"),
    MEDICINE("Medicine", "💊", "Gentle Reminder"),
    WORKOUT("Workout", "⚡", "Upbeat Energy"),
    CUSTOM("Custom", "✨", "Classic Bell")
}
