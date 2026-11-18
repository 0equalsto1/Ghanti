package com.example.omniclock.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.omniclock.service.AlarmScheduler
import com.example.omniclock.service.AlarmService
import com.example.omniclock.ui.ring.AlarmRingActivity

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_ID) ?: return
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TITLE) ?: "Alarm"
        val eventType = intent.getStringExtra(AlarmScheduler.EXTRA_EVENT_TYPE) ?: "GENERAL"
        val isVibrate = intent.getBooleanExtra(AlarmScheduler.EXTRA_IS_VIBRATE, true)

        // 1. Start foreground alarm playback service
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmScheduler.EXTRA_EVENT_TYPE, eventType)
            putExtra(AlarmScheduler.EXTRA_IS_VIBRATE, isVibrate)
        }
        context.startForegroundService(serviceIntent)

        // 2. Launch Fullscreen Ring & Snooze Activity
        val activityIntent = Intent(context, AlarmRingActivity::class.java).apply {
            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarmId)
            putExtra(AlarmScheduler.EXTRA_ALARM_TITLE, title)
            putExtra(AlarmScheduler.EXTRA_EVENT_TYPE, eventType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        context.startActivity(activityIntent)
    }
}
