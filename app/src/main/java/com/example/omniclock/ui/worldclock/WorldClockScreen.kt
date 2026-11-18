package com.example.omniclock.ui.worldclock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.omniclock.data.model.WorldClockCity
import com.example.omniclock.data.repository.WorldClockRepository
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WorldClockScreen(repository: WorldClockRepository) {
    val cities by repository.cities.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    // Re-trigger recomposition every second for live clock tick
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentTimeMillis = System.currentTimeMillis()
        }
    }

    val filteredCities = remember(cities, searchQuery, currentTimeMillis) {
        repository.searchCities(searchQuery)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Hero Local Time Card
        LocalTimeHeroCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Search Major Countries / Cities
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search country or city (e.g., Tokyo, London, Paris)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // City List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(filteredCities, key = { it.id }) { city ->
                WorldClockCityCard(
                    city = city,
                    onTogglePin = { repository.togglePin(city.id) }
                )
            }
        }
    }
}

@Composable
fun LocalTimeHeroCard() {
    val localDateTime = ZonedDateTime.now()
    val formattedTime = localDateTime.format(DateTimeFormatter.ofPattern("hh:mm:ss a"))
    val formattedDate = localDateTime.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📍 Local Time (${localDateTime.zone.id})",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedTime,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = formattedDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun WorldClockCityCard(
    city: WorldClockCity,
    onTogglePin: () -> Unit
) {
    val isDay = city.currentZonedDateTime.hour in 6..18

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = city.flagEmoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = city.cityName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "${city.countryName} · ${city.timeDifferenceFromLocal()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = city.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Day/Night Indicator
                Icon(
                    imageVector = if (isDay) Icons.Outlined.WbSunny else Icons.Outlined.NightsStay,
                    contentDescription = if (isDay) "Daytime" else "Nighttime",
                    tint = if (isDay) Color(0xFFFFB300) else Color(0xFF90CAF9),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = city.formattedTime,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = onTogglePin) {
                    Icon(
                        imageVector = if (city.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                        contentDescription = "Pin City",
                        tint = if (city.isPinned) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}
