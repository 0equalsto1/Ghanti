package com.example.omniclock.ui.alarm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.omniclock.data.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAlarmDialog(
    initialAlarm: Alarm? = null,
    onDismiss: () -> Unit,
    onSave: (Alarm) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialAlarm?.hour ?: 7) }
    var selectedMinute by remember { mutableIntStateOf(initialAlarm?.minute ?: 0) }
    var label by remember { mutableStateOf(initialAlarm?.label ?: "") }
    var selectedEventType by remember { mutableStateOf(initialAlarm?.eventType ?: EventType.GENERAL) }
    var recurrencePreset by remember { mutableStateOf(initialAlarm?.recurrence?.preset ?: RecurrencePreset.WEEKDAYS) }
    var selectedDates by remember { mutableStateOf(initialAlarm?.recurrence?.specificDates ?: setOf(LocalDate.now())) }
    var isVibrate by remember { mutableStateOf(initialAlarm?.isVibrate ?: true) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Title
                Text(
                    text = if (initialAlarm == null) "New Alarm" else "Edit Alarm",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Time Display & Adjuster
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TimeNumberPicker(
                        value = selectedHour,
                        range = 0..23,
                        label = "Hour",
                        onValueChange = { selectedHour = it }
                    )
                    Text(":", fontSize = 32.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                    TimeNumberPicker(
                        value = selectedMinute,
                        range = 0..59,
                        label = "Minute",
                        onValueChange = { selectedMinute = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Event Type Category
                Text("Event Type", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(EventType.values()) { type ->
                        FilterChip(
                            selected = selectedEventType == type,
                            onClick = { selectedEventType = type },
                            label = { Text("${type.emoji} ${type.title}") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Event Label / Note
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Event Name / Memo") },
                    placeholder = { Text("e.g., Mom's Birthday, Team Standup") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Recurrence Presets
                Text("Repeat Schedule", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PresetChip(
                            text = "Every Day",
                            selected = recurrencePreset == RecurrencePreset.EVERY_DAY,
                            modifier = Modifier.weight(1f),
                            onClick = { recurrencePreset = RecurrencePreset.EVERY_DAY }
                        )
                        PresetChip(
                            text = "Weekdays",
                            selected = recurrencePreset == RecurrencePreset.WEEKDAYS,
                            modifier = Modifier.weight(1f),
                            onClick = { recurrencePreset = RecurrencePreset.WEEKDAYS }
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PresetChip(
                            text = "Whole Week",
                            selected = recurrencePreset == RecurrencePreset.WHOLE_WEEK,
                            modifier = Modifier.weight(1f),
                            onClick = { recurrencePreset = RecurrencePreset.WHOLE_WEEK }
                        )
                        PresetChip(
                            text = "Specific Date(s)",
                            selected = recurrencePreset == RecurrencePreset.SPECIFIC_DATES,
                            modifier = Modifier.weight(1f),
                            onClick = { recurrencePreset = RecurrencePreset.SPECIFIC_DATES }
                        )
                    }
                }

                // If "Specific Date(s)" is selected, show multiple date picker
                if (recurrencePreset == RecurrencePreset.SPECIFIC_DATES) {
                    Spacer(modifier = Modifier.height(12.dp))
                    MultiDatePickerSection(
                        selectedDates = selectedDates,
                        onAddDate = { date -> selectedDates = selectedDates + date },
                        onRemoveDate = { date -> selectedDates = selectedDates - date }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Vibration Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Vibrate", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = isVibrate, onCheckedChange = { isVibrate = it })
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val rule = RecurrenceRule(
                                preset = recurrencePreset,
                                specificDates = if (recurrencePreset == RecurrencePreset.SPECIFIC_DATES) selectedDates else emptySet()
                            )
                            val alarm = (initialAlarm ?: Alarm(hour = selectedHour, minute = selectedMinute)).copy(
                                hour = selectedHour,
                                minute = selectedMinute,
                                label = label,
                                eventType = selectedEventType,
                                recurrence = rule,
                                isVibrate = isVibrate,
                                isEnabled = true
                            )
                            onSave(alarm)
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Alarm")
                    }
                }
            }
        }
    }
}

@Composable
fun PresetChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    )
}

@Composable
fun MultiDatePickerSection(
    selectedDates: Set<LocalDate>,
    onAddDate: (LocalDate) -> Unit,
    onRemoveDate: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    var candidateDateOffset by remember { mutableIntStateOf(0) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Select Multiple Dates:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            // Quick multi-date add row (Next 7 days)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(14) { dayOffset ->
                    val date = today.plusDays(dayOffset.toLong())
                    val isSelected = date in selectedDates
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                            .clickable {
                                if (isSelected) onRemoveDate(date) else onAddDate(date)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                date.format(DateTimeFormatter.ofPattern("EEE")),
                                fontSize = 11.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                date.dayOfMonth.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // List of selected dates
            if (selectedDates.isNotEmpty()) {
                Text("Selected Dates (${selectedDates.size}):", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(selectedDates.sorted()) { date ->
                        InputChip(
                            selected = true,
                            onClick = { onRemoveDate(date) },
                            label = { Text(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))) },
                            trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeNumberPicker(
    value: Int,
    range: IntRange,
    label: String,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            val next = if (value + 1 > range.last) range.first else value + 1
            onValueChange(next)
        }) {
            Text("▲", fontSize = 18.sp)
        }
        Text(
            text = String.format("%02d", value),
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        IconButton(onClick = {
            val prev = if (value - 1 < range.first) range.last else value - 1
            onValueChange(prev)
        }) {
            Text("▼", fontSize = 18.sp)
        }
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}
