package com.example.omniclock.ui.alarm

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniclock.data.model.Alarm
import com.example.omniclock.data.repository.AlarmRepository

@Composable
fun AlarmListScreen(
    repository: AlarmRepository,
    onTestRingAlarm: (Alarm) -> Unit
) {
    val alarms by repository.alarms.collectAsState()
    var editingAlarm by remember { mutableStateOf<Alarm?>(null) }
    var isAddingAlarm by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { isAddingAlarm = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Alarm") },
                text = { Text("Add Alarm") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            if (alarms.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No alarms set.\nTap '+' to create your first event alarm!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            onToggle = { repository.toggleAlarm(alarm.id) },
                            onClick = { editingAlarm = alarm },
                            onDelete = { repository.deleteAlarm(alarm.id) },
                            onTestRing = { onTestRingAlarm(alarm) }
                        )
                    }
                }
            }
        }
    }

    if (isAddingAlarm) {
        AddEditAlarmDialog(
            initialAlarm = null,
            onDismiss = { isAddingAlarm = false },
            onSave = {
                repository.addOrUpdateAlarm(it)
                isAddingAlarm = false
            }
        )
    }

    editingAlarm?.let { alarm ->
        AddEditAlarmDialog(
            initialAlarm = alarm,
            onDismiss = { editingAlarm = null },
            onSave = {
                repository.addOrUpdateAlarm(it)
                editingAlarm = null
            }
        )
    }
}

@Composable
fun AlarmCard(
    alarm: Alarm,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onTestRing: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Event Type Badge & Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alarm.displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time Display
                Text(
                    text = alarm.formattedTime,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (alarm.isEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                )

                // Recurrence Description
                Text(
                    text = "🗓️ ${alarm.recurrence.describe()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (alarm.isEnabled) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
                )

                Spacer(modifier = Modifier.height(4.dp))
                // Quick Preview Ring Button
                TextButton(
                    onClick = onTestRing,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("🔔 Test Ring Screen", fontSize = 12.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = alarm.isEnabled,
                    onCheckedChange = { onToggle() }
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Alarm",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}
