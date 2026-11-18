package com.example.omniclock.data.repository

import android.content.Context
import com.example.omniclock.data.model.Alarm
import com.example.omniclock.data.model.EventType
import com.example.omniclock.data.model.RecurrencePreset
import com.example.omniclock.data.model.RecurrenceRule
import com.example.omniclock.service.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class AlarmRepository(private val context: Context) {

    private val scheduler = AlarmScheduler(context)

    // Prepopulate with intuitive demo alarms
    private val _alarms = MutableStateFlow(
        listOf(
            Alarm(
                id = "demo-1",
                hour = 7,
                minute = 0,
                isEnabled = true,
                label = "Morning Wake Up",
                eventType = EventType.GENERAL,
                recurrence = RecurrenceRule(preset = RecurrencePreset.WEEKDAYS)
            ),
            Alarm(
                id = "demo-2",
                hour = 8,
                minute = 30,
                isEnabled = true,
                label = "Mom's Birthday Celebration",
                eventType = EventType.BIRTHDAY,
                recurrence = RecurrenceRule(
                    preset = RecurrencePreset.SPECIFIC_DATES,
                    specificDates = setOf(LocalDate.now(), LocalDate.now().plusMonths(1))
                )
            ),
            Alarm(
                id = "demo-3",
                hour = 19,
                minute = 0,
                isEnabled = false,
                label = "Wedding Anniversary Dinner",
                eventType = EventType.ANNIVERSARY,
                recurrence = RecurrenceRule(preset = RecurrencePreset.ONCE)
            )
        )
    )
    val alarms: StateFlow<List<Alarm>> = _alarms.asStateFlow()

    fun addOrUpdateAlarm(alarm: Alarm) {
        val current = _alarms.value.toMutableList()
        val index = current.indexOfFirst { it.id == alarm.id }
        if (index != -1) {
            current[index] = alarm
        } else {
            current.add(alarm)
        }
        _alarms.value = current.sortedWith(compareBy({ it.hour }, { it.minute }))

        if (alarm.isEnabled) {
            scheduler.schedule(alarm)
        } else {
            scheduler.cancel(alarm)
        }
    }

    fun toggleAlarm(id: String) {
        val current = _alarms.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val updated = current[index].copy(isEnabled = !current[index].isEnabled)
            current[index] = updated
            _alarms.value = current

            if (updated.isEnabled) {
                scheduler.schedule(updated)
            } else {
                scheduler.cancel(updated)
            }
        }
    }

    fun deleteAlarm(id: String) {
        val alarm = _alarms.value.find { it.id == id }
        if (alarm != null) {
            scheduler.cancel(alarm)
            _alarms.value = _alarms.value.filter { it.id != id }
        }
    }

    fun getAlarmById(id: String): Alarm? {
        return _alarms.value.find { it.id == id }
    }
}
