package com.example.omniclock.data.repository

import com.example.omniclock.data.model.WorldClockCity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.ZoneId

class WorldClockRepository {

    private val allCities = listOf(
        WorldClockCity("tokyo", "Tokyo", "Japan", "🇯🇵", ZoneId.of("Asia/Tokyo"), isPinned = true),
        WorldClockCity("london", "London", "United Kingdom", "🇬🇧", ZoneId.of("Europe/London"), isPinned = true),
        WorldClockCity("newyork", "New York", "United States", "🇺🇸", ZoneId.of("America/New_York"), isPinned = true),
        WorldClockCity("sanfrancisco", "San Francisco", "United States", "🇺🇸", ZoneId.of("America/Los_Angeles")),
        WorldClockCity("paris", "Paris", "France", "🇫🇷", ZoneId.of("Europe/Paris"), isPinned = true),
        WorldClockCity("berlin", "Berlin", "Germany", "🇩🇪", ZoneId.of("Europe/Berlin")),
        WorldClockCity("dubai", "Dubai", "United Arab Emirates", "🇦🇪", ZoneId.of("Asia/Dubai"), isPinned = true),
        WorldClockCity("mumbai", "New Delhi / Mumbai", "India", "🇮🇳", ZoneId.of("Asia/Kolkata"), isPinned = true),
        WorldClockCity("singapore", "Singapore", "Singapore", "🇸🇬", ZoneId.of("Asia/Singapore"), isPinned = true),
        WorldClockCity("sydney", "Sydney", "Australia", "🇦🇺", ZoneId.of("Australia/Sydney")),
        WorldClockCity("toronto", "Toronto", "Canada", "🇨🇦", ZoneId.of("America/Toronto")),
        WorldClockCity("saopaulo", "São Paulo", "Brazil", "🇧🇷", ZoneId.of("America/Sao_Paulo")),
        WorldClockCity("cairo", "Cairo", "Egypt", "🇪🇬", ZoneId.of("Africa/Cairo")),
        WorldClockCity("seoul", "Seoul", "South Korea", "🇰🇷", ZoneId.of("Asia/Seoul")),
        WorldClockCity("hongkong", "Hong Kong", "Hong Kong", "🇭🇰", ZoneId.of("Asia/Hong_Kong")),
        WorldClockCity("zurich", "Zurich", "Switzerland", "🇨🇭", ZoneId.of("Europe/Zurich")),
        WorldClockCity("moscow", "Moscow", "Russia", "🇷🇺", ZoneId.of("Europe/Moscow")),
        WorldClockCity("johannesburg", "Johannesburg", "South Africa", "🇿🇦", ZoneId.of("Africa/Johannesburg")),
        WorldClockCity("auckland", "Auckland", "New Zealand", "🇳🇿", ZoneId.of("Pacific/Auckland")),
        WorldClockCity("mexicocity", "Mexico City", "Mexico", "🇲🇽", ZoneId.of("America/Mexico_City"))
    )

    private val _cities = MutableStateFlow(allCities)
    val cities: StateFlow<List<WorldClockCity>> = _cities.asStateFlow()

    fun togglePin(id: String) {
        _cities.value = _cities.value.map {
            if (it.id == id) it.copy(isPinned = !it.isPinned) else it
        }.sortedWith(compareByDescending<WorldClockCity> { it.isPinned }.thenBy { it.cityName })
    }

    fun searchCities(query: String): List<WorldClockCity> {
        if (query.isBlank()) return _cities.value
        return _cities.value.filter {
            it.cityName.contains(query, ignoreCase = true) ||
            it.countryName.contains(query, ignoreCase = true)
        }
    }
}
