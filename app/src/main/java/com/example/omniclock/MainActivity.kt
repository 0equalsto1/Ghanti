package com.example.omniclock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.omniclock.data.model.Alarm
import com.example.omniclock.data.repository.AlarmRepository
import com.example.omniclock.data.repository.WorldClockRepository
import com.example.omniclock.service.AlarmScheduler
import com.example.omniclock.ui.alarm.AlarmListScreen
import com.example.omniclock.ui.ring.AlarmRingActivity
import com.example.omniclock.ui.theme.OmniClockTheme
import com.example.omniclock.ui.worldclock.WorldClockScreen

class MainActivity : ComponentActivity() {

    private lateinit var worldClockRepository: WorldClockRepository
    private lateinit var alarmRepository: AlarmRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        worldClockRepository = WorldClockRepository()
        alarmRepository = AlarmRepository(this)

        setContent {
            OmniClockTheme {
                MainAppScaffold(
                    worldClockRepository = worldClockRepository,
                    alarmRepository = alarmRepository,
                    onTestRingAlarm = { alarm ->
                        val intent = Intent(this, AlarmRingActivity::class.java).apply {
                            putExtra(AlarmScheduler.EXTRA_ALARM_ID, alarm.id)
                            putExtra(AlarmScheduler.EXTRA_ALARM_TITLE, alarm.displayTitle)
                            putExtra(AlarmScheduler.EXTRA_EVENT_TYPE, alarm.eventType.name)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    worldClockRepository: WorldClockRepository,
    alarmRepository: AlarmRepository,
    onTestRingAlarm: (Alarm) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("OmniClock", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Public, contentDescription = "World Clock") },
                    label = { Text("World Clock") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Alarm, contentDescription = "Alarms") },
                    label = { Text("Event Alarms") }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> WorldClockScreen(repository = worldClockRepository)
                1 -> AlarmListScreen(
                    repository = alarmRepository,
                    onTestRingAlarm = onTestRingAlarm
                )
            }
        }
    }
}
