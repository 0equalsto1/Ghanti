package com.example.omniclock.ui.ring

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.omniclock.data.model.EventType
import com.example.omniclock.service.AlarmScheduler
import com.example.omniclock.service.AlarmService
import com.example.omniclock.ui.theme.OmniClockTheme

class AlarmRingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unlockAndTurnScreenOn()

        val alarmId = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_ID) ?: ""
        val title = intent.getStringExtra(AlarmScheduler.EXTRA_ALARM_TITLE) ?: "Alarm Alert"
        val eventTypeName = intent.getStringExtra(AlarmScheduler.EXTRA_EVENT_TYPE) ?: "GENERAL"
        val eventType = try {
            EventType.valueOf(eventTypeName)
        } catch (_: Exception) {
            EventType.GENERAL
        }

        setContent {
            OmniClockTheme {
                AlarmRingScreen(
                    title = title,
                    eventTypeEmoji = eventType.emoji,
                    onSnooze = { minutes ->
                        // 1. Stop audio/vibration service
                        stopAlarmService()
                        // 2. Schedule snooze alarm
                        val scheduler = AlarmScheduler(this)
                        scheduler.snooze(alarmId, title, eventTypeName, minutes)
                        finish()
                    },
                    onDismiss = {
                        // Stop audio/vibration service
                        stopAlarmService()
                        finish()
                    }
                )
            }
        }
    }

    private fun stopAlarmService() {
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
        }
        startService(stopIntent)
    }

    private fun unlockAndTurnScreenOn() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}
