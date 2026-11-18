package com.example.omniclock.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.omniclock.data.model.Alarm
import com.example.omniclock.receiver.AlarmReceiver
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(alarm: Alarm) {
        val triggerTime = computeNextTriggerEpochMillis(alarm) ?: return

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarm.id)
            putExtra(EXTRA_ALARM_TITLE, alarm.displayTitle)
            putExtra(EXTRA_EVENT_TYPE, alarm.eventType.name)
            putExtra(EXTRA_IS_VIBRATE, alarm.isVibrate)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun snooze(alarmId: String, title: String, eventType: String, minutes: Int) {
        val triggerTime = System.currentTimeMillis() + (minutes * 60 * 1000L)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(EXTRA_ALARM_ID, alarmId)
            putExtra(EXTRA_ALARM_TITLE, title)
            putExtra(EXTRA_EVENT_TYPE, eventType)
            putExtra(EXTRA_IS_SNOOZE, true)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    fun cancel(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun computeNextTriggerEpochMillis(alarm: Alarm): Long? {
        val now = LocalDateTime.now()
        val alarmTime = LocalTime.of(alarm.hour, alarm.minute)

        var candidateDate = LocalDate.now()

        // Check up to 365 days ahead for matching date
        for (i in 0..365) {
            val dateToCheck = candidateDate.plusDays(i.toLong())
            val candidateDateTime = LocalDateTime.of(dateToCheck, alarmTime)

            if (candidateDateTime.isAfter(now) && alarm.recurrence.matchesDate(dateToCheck)) {
                return candidateDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            }
        }
        return null
    }

    companion object {
        const val EXTRA_ALARM_ID = "extra_alarm_id"
        const val EXTRA_ALARM_TITLE = "extra_alarm_title"
        const val EXTRA_EVENT_TYPE = "extra_event_type"
        const val EXTRA_IS_VIBRATE = "extra_is_vibrate"
        const val EXTRA_IS_SNOOZE = "extra_is_snooze"
    }
}
