package com.example.omniclock.data.model

import java.time.DayOfWeek
import java.time.LocalDate

enum class RecurrencePreset(val label: String) {
    ONCE("Once"),
    EVERY_DAY("Every Day"),
    WEEKDAYS("Weekdays (Mon-Fri)"),
    WEEKENDS("Weekends (Sat-Sun)"),
    WHOLE_WEEK("Whole Week"),
    SPECIFIC_DATES("Specific Date(s)")
}

data class RecurrenceRule(
    val preset: RecurrencePreset = RecurrencePreset.ONCE,
    val selectedDays: Set<DayOfWeek> = emptySet(),
    val specificDates: Set<LocalDate> = emptySet()
) {
    fun matchesDate(date: LocalDate): Boolean {
        return when (preset) {
            RecurrencePreset.ONCE -> true
            RecurrencePreset.EVERY_DAY -> true
            RecurrencePreset.WEEKDAYS -> date.dayOfWeek in listOf(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            )
            RecurrencePreset.WEEKENDS -> date.dayOfWeek in listOf(
                DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
            )
            RecurrencePreset.WHOLE_WEEK -> true
            RecurrencePreset.SPECIFIC_DATES -> date in specificDates
        }
    }

    fun describe(): String {
        return when (preset) {
            RecurrencePreset.ONCE -> "Once"
            RecurrencePreset.EVERY_DAY -> "Every day"
            RecurrencePreset.WEEKDAYS -> "Mon - Fri"
            RecurrencePreset.WEEKENDS -> "Sat, Sun"
            RecurrencePreset.WHOLE_WEEK -> "Whole week"
            RecurrencePreset.SPECIFIC_DATES -> {
                if (specificDates.isEmpty()) "No dates selected"
                else if (specificDates.size == 1) specificDates.first().toString()
                else "${specificDates.size} specific dates"
            }
        }
    }
}
