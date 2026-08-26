package com.example.ui.screens.prayer

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicDataSource
import com.example.domain.calculator.CalculationMethod
import com.example.domain.calculator.Madhhab
import com.example.domain.calculator.PrayerType
import com.example.ui.MainViewModel
import com.example.ui.components.IslamicBannerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val homeState by viewModel.homeUiState.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    val timeFormat = remember(settings.is24HourFormat) {
        SimpleDateFormat(if (settings.is24HourFormat) "HH:mm" else "hh:mm a", Locale.getDefault())
    }
    val hourMin24Format = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    var showCityPicker by remember { mutableStateOf(false) }
    var showMethodPicker by remember { mutableStateOf(false) }
    var showCustomTimesDialog by remember { mutableStateOf(false) }
    var selectedFilterPrayer by remember { mutableStateOf("ALL") }
    var expandedPrayerName by remember { mutableStateOf<String?>(null) }

    val result = homeState.prayerTimes

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Location & Calculation Info Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showCityPicker = true }
                                .testTag("select_prayer_city_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${settings.cityName}, ${settings.countryName}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.Filled.ArrowDropDown,
                                contentDescription = "Select City",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 12H / 24H Quick Toggle Chip
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.toggle24HourFormat() }
                                    .testTag("toggle_12_24_hour_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Schedule,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (settings.is24HourFormat) "24H" else "12H",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(4.dp))

                            IconButton(
                                onClick = { showCustomTimesDialog = true },
                                modifier = Modifier.testTag("custom_prayer_times_button")
                            ) {
                                Icon(
                                    imageVector = if (settings.useCustomPrayerTimes) Icons.Filled.EditCalendar else Icons.Outlined.EditCalendar,
                                    contentDescription = "Custom Prayer Times",
                                    tint = if (settings.useCustomPrayerTimes) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = { showMethodPicker = true },
                                modifier = Modifier.testTag("calculation_settings_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Tune,
                                    contentDescription = "Calculation Settings",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    if (settings.useCustomPrayerTimes) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = "Custom Prayer Times Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Text(
                            text = "Method: ${settings.calculationMethod.displayName} • ${settings.madhhab.name} Asr",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                        )
                    }
                }
            }
        }

        // Live Next Prayer Hero
        if (result != null) {
            item {
                val totalSec = (result.timeRemainingMillis / 1000).coerceAtLeast(0)
                val hours = totalSec / 3600
                val minutes = (totalSec % 3600) / 60
                val seconds = totalSec % 60
                val formattedCountdown = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

                IslamicBannerCard(
                    title = "Next: ${result.currentOrNextPrayer.displayName} (${result.currentOrNextPrayer.arabicName})",
                    subtitle = "Time remaining: $formattedCountdown • Adhan at ${timeFormat.format(result.nextPrayerTime)}",
                    arabicGreeting = "حَيَّ عَلَى الصَّلَاةِ • حَيَّ عَلَى الْفَلَاحِ",
                    icon = Icons.Filled.AccessTime
                )
            }

            // Quick Selection & Filter Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filterOptions = listOf(
                        "ALL" to "All Schedule",
                        "FAJR" to "Fajr (الفجر)",
                        "DHUHR" to "Dhuhr (الظهر)",
                        "ASR" to "Asr (العصر)",
                        "MAGHRIB" to "Maghrib (المغرب)",
                        "ISHA" to "Isha (العشاء)",
                        "QIYAM" to "Tahajjud (القيام)"
                    )
                    items(filterOptions) { (key, label) ->
                        val isSelected = selectedFilterPrayer == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilterPrayer = key },
                            label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("prayer_filter_$key")
                        )
                    }
                }
            }

            // Daily 5 Obligatory Prayers + Sunrise + Tahajjud list
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(
                        title = "Today's Prayer Schedule",
                        subtitle = homeState.hijriDate?.formatShort()
                    )
                    TextButton(
                        onClick = { showCustomTimesDialog = true },
                        modifier = Modifier.testTag("edit_adhan_link")
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit Times", fontSize = 12.sp)
                    }
                }
            }

            val prayerList = listOf(
                PrayerRowData(
                    key = "FAJR",
                    name = "Fajr",
                    arabic = "الفجر",
                    rakahs = "2 Sunnah + 2 Fard",
                    time = result.fajr,
                    customTimeStr = settings.customFajrTime,
                    isNext = result.currentOrNextPrayer == PrayerType.FAJR,
                    icon = Icons.Filled.WbTwilight,
                    notifEnabled = settings.fajrNotification,
                    onToggleNotif = { viewModel.updatePrayerNotification("fajr", !settings.fajrNotification) }
                ),
                PrayerRowData(
                    key = "SUNRISE",
                    name = "Sunrise (Shuruq)",
                    arabic = "الشروق",
                    rakahs = "Duha time begins",
                    time = result.sunrise,
                    customTimeStr = "",
                    isNext = false,
                    icon = Icons.Filled.WbSunny,
                    notifEnabled = false,
                    onToggleNotif = {}
                ),
                PrayerRowData(
                    key = "DHUHR",
                    name = "Dhuhr",
                    arabic = "الظهر",
                    rakahs = "4 Sunnah + 4 Fard + 2 Sunnah",
                    time = result.dhuhr,
                    customTimeStr = settings.customDhuhrTime,
                    isNext = result.currentOrNextPrayer == PrayerType.DHUHR,
                    icon = Icons.Filled.LightMode,
                    notifEnabled = settings.dhuhrNotification,
                    onToggleNotif = { viewModel.updatePrayerNotification("dhuhr", !settings.dhuhrNotification) }
                ),
                PrayerRowData(
                    key = "ASR",
                    name = "Asr",
                    arabic = "العصر",
                    rakahs = "4 Fard",
                    time = result.asr,
                    customTimeStr = settings.customAsrTime,
                    isNext = result.currentOrNextPrayer == PrayerType.ASR,
                    icon = Icons.Filled.FilterDrama,
                    notifEnabled = settings.asrNotification,
                    onToggleNotif = { viewModel.updatePrayerNotification("asr", !settings.asrNotification) }
                ),
                PrayerRowData(
                    key = "MAGHRIB",
                    name = "Maghrib (Iftar)",
                    arabic = "المغرب",
                    rakahs = "3 Fard + 2 Sunnah",
                    time = result.maghrib,
                    customTimeStr = settings.customMaghribTime,
                    isNext = result.currentOrNextPrayer == PrayerType.MAGHRIB,
                    icon = Icons.Filled.Bedtime,
                    notifEnabled = settings.maghribNotification,
                    onToggleNotif = { viewModel.updatePrayerNotification("maghrib", !settings.maghribNotification) }
                ),
                PrayerRowData(
                    key = "ISHA",
                    name = "Isha",
                    arabic = "العشاء",
                    rakahs = "4 Fard + 2 Sunnah + 3 Witr",
                    time = result.isha,
                    customTimeStr = settings.customIshaTime,
                    isNext = result.currentOrNextPrayer == PrayerType.ISHA,
                    icon = Icons.Filled.NightsStay,
                    notifEnabled = settings.ishaNotification,
                    onToggleNotif = { viewModel.updatePrayerNotification("isha", !settings.ishaNotification) }
                )
            )

            val filteredList = if (selectedFilterPrayer == "ALL") {
                prayerList
            } else if (selectedFilterPrayer == "QIYAM") {
                emptyList()
            } else {
                prayerList.filter { it.key == selectedFilterPrayer }
            }

            items(filteredList) { prayer ->
                val isExpanded = expandedPrayerName == prayer.key
                PrayerTimeCard(
                    prayer = prayer,
                    timeFormat = timeFormat,
                    isExpanded = isExpanded,
                    onCardClick = {
                        expandedPrayerName = if (isExpanded) null else prayer.key
                    },
                    onEditTime = {
                        showCustomTimesDialog = true
                    }
                )
            }

            // Midnight & Tahajjud Special Times
            if (selectedFilterPrayer == "ALL" || selectedFilterPrayer == "QIYAM") {
                item {
                    SectionHeader(title = "Night Worship & Tahajjud (Qiyam)")
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_night_worship"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Islamic Midnight", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Halfway between Maghrib & Fajr", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = timeFormat.format(result.midnight),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Last Third of Night (Tahajjud)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Prime time for Dua & Istighfar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = timeFormat.format(result.lastThirdOfNight),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Prayer & Adhan Times Dialog
    if (showCustomTimesDialog && result != null) {
        var editUseCustom by remember { mutableStateOf(settings.useCustomPrayerTimes) }
        var editFajr by remember { mutableStateOf(settings.customFajrTime.ifBlank { hourMin24Format.format(result.fajr) }) }
        var editDhuhr by remember { mutableStateOf(settings.customDhuhrTime.ifBlank { hourMin24Format.format(result.dhuhr) }) }
        var editAsr by remember { mutableStateOf(settings.customAsrTime.ifBlank { hourMin24Format.format(result.asr) }) }
        var editMaghrib by remember { mutableStateOf(settings.customMaghribTime.ifBlank { hourMin24Format.format(result.maghrib) }) }
        var editIsha by remember { mutableStateOf(settings.customIshaTime.ifBlank { hourMin24Format.format(result.isha) }) }
        var selectedAdhanSound by remember { mutableStateOf(settings.adhanSoundName) }

        val adhanSounds = listOf("Makkah Adhan", "Madinah Adhan", "Al-Aqsa Adhan", "Traditional Adhan", "Gentle Beep", "Silent Vibration")

        AlertDialog(
            onDismissRequest = { showCustomTimesDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.EditCalendar, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Custom Prayer & Adhan Times", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Enable Custom Times", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Override astronomical calculations with your local mosque timetable.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                                }
                                Switch(
                                    checked = editUseCustom,
                                    onCheckedChange = { editUseCustom = it },
                                    modifier = Modifier.testTag("toggle_custom_prayer_times_switch")
                                )
                            }
                        }
                    }

                    item {
                        Text("Prayer Times (24h format HH:mm):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Fajr Time Picker Input
                    item {
                        CustomTimeInputRow(
                            label = "Fajr",
                            timeValue = editFajr,
                            enabled = editUseCustom,
                            onTimeChange = { editFajr = it }
                        )
                    }
                    // Dhuhr Time Picker Input
                    item {
                        CustomTimeInputRow(
                            label = "Dhuhr",
                            timeValue = editDhuhr,
                            enabled = editUseCustom,
                            onTimeChange = { editDhuhr = it }
                        )
                    }
                    // Asr Time Picker Input
                    item {
                        CustomTimeInputRow(
                            label = "Asr",
                            timeValue = editAsr,
                            enabled = editUseCustom,
                            onTimeChange = { editAsr = it }
                        )
                    }
                    // Maghrib Time Picker Input
                    item {
                        CustomTimeInputRow(
                            label = "Maghrib",
                            timeValue = editMaghrib,
                            enabled = editUseCustom,
                            onTimeChange = { editMaghrib = it }
                        )
                    }
                    // Isha Time Picker Input
                    item {
                        CustomTimeInputRow(
                            label = "Isha",
                            timeValue = editIsha,
                            enabled = editUseCustom,
                            onTimeChange = { editIsha = it }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Adhan Voice / Sound:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    items(adhanSounds) { sound ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAdhanSound = sound }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(sound, fontSize = 13.sp, fontWeight = if (selectedAdhanSound == sound) FontWeight.Bold else FontWeight.Normal)
                            if (selectedAdhanSound == sound) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                val defFajr = hourMin24Format.format(result.fajr)
                                val defDhuhr = hourMin24Format.format(result.dhuhr)
                                val defAsr = hourMin24Format.format(result.asr)
                                val defMaghrib = hourMin24Format.format(result.maghrib)
                                val defIsha = hourMin24Format.format(result.isha)
                                viewModel.resetCustomPrayerTimes(defFajr, defDhuhr, defAsr, defMaghrib, defIsha)
                                editUseCustom = false
                                editFajr = defFajr
                                editDhuhr = defDhuhr
                                editAsr = defAsr
                                editMaghrib = defMaghrib
                                editIsha = defIsha
                                Toast.makeText(context, "Reset to astronomical calculation", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().testTag("reset_custom_times_button")
                        ) {
                            Icon(Icons.Filled.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset to Astronomical Calculator")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setUseCustomPrayerTimes(editUseCustom)
                        viewModel.updateCustomPrayerTime("fajr", editFajr)
                        viewModel.updateCustomPrayerTime("dhuhr", editDhuhr)
                        viewModel.updateCustomPrayerTime("asr", editAsr)
                        viewModel.updateCustomPrayerTime("maghrib", editMaghrib)
                        viewModel.updateCustomPrayerTime("isha", editIsha)
                        viewModel.updateAdhanSound(selectedAdhanSound)
                        showCustomTimesDialog = false
                        Toast.makeText(context, "Prayer times updated successfully", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("save_custom_prayer_times_button")
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomTimesDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // City Picker Dialog
    if (showCityPicker) {
        AlertDialog(
            onDismissRequest = { showCityPicker = false },
            title = { Text("Select City / Location", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(320.dp)) {
                    items(IslamicDataSource.POPULAR_CITIES) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCity(city)
                                    showCityPicker = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(city.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(city.country, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (settings.cityName == city.name) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityPicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Calculation Method Dialog
    if (showMethodPicker) {
        AlertDialog(
            onDismissRequest = { showMethodPicker = false },
            title = { Text("Calculation Method & Madhhab", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(340.dp)) {
                    item {
                        Text("Calculation Authority:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(CalculationMethod.values()) { method ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { viewModel.preferencesRepository.updateCalculationMethod(method) }
                                }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(method.displayName, fontSize = 13.sp, fontWeight = if (settings.calculationMethod == method) FontWeight.Bold else FontWeight.Normal)
                            if (settings.calculationMethod == method) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Asr Juristic Method (Madhhab):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(Madhhab.values()) { madhhab ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch { viewModel.preferencesRepository.updateMadhhab(madhhab) }
                                }
                                .padding(vertical = 10.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                if (madhhab == Madhhab.STANDARD) "Standard (Shafi'i, Maliki, Hanbali)" else "Hanafi (Later Asr)",
                                fontSize = 13.sp,
                                fontWeight = if (settings.madhhab == madhhab) FontWeight.Bold else FontWeight.Normal
                            )
                            if (settings.madhhab == madhhab) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodPicker = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun CustomTimeInputRow(
    label: String,
    timeValue: String,
    enabled: Boolean,
    onTimeChange: (String) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium, fontSize = 14.sp)
        OutlinedButton(
            onClick = {
                if (enabled) {
                    val parts = timeValue.split(":")
                    val h = parts.getOrNull(0)?.toIntOrNull() ?: 12
                    val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                    TimePickerDialog(context, { _, hourOfDay, minute ->
                        onTimeChange(String.format(Locale.US, "%02d:%02d", hourOfDay, minute))
                    }, h, m, true).show()
                }
            },
            enabled = enabled,
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(timeValue.ifBlank { "--:--" }, fontWeight = FontWeight.Bold)
        }
    }
}

data class PrayerRowData(
    val key: String,
    val name: String,
    val arabic: String,
    val rakahs: String,
    val time: Date,
    val customTimeStr: String,
    val isNext: Boolean,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val notifEnabled: Boolean,
    val onToggleNotif: () -> Unit
)

@Composable
fun PrayerTimeCard(
    prayer: PrayerRowData,
    timeFormat: SimpleDateFormat,
    isExpanded: Boolean = false,
    onCardClick: () -> Unit = {},
    onEditTime: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("prayer_card_${prayer.name.lowercase().substringBefore(" ")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prayer.isNext) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(if (prayer.isNext) 4.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (prayer.isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = prayer.icon,
                            contentDescription = null,
                            tint = if (prayer.isNext) Color.White else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = prayer.name,
                                fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = if (prayer.isNext) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                            if (prayer.isNext) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary
                                ) {
                                    Text(
                                        text = "NEXT",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = prayer.arabic,
                            fontSize = 13.sp,
                            color = if (prayer.isNext) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeFormat.format(prayer.time),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (prayer.isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )

                    if (prayer.key != "SUNRISE") {
                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = prayer.onToggleNotif,
                            modifier = Modifier.size(36.dp).testTag("prayer_notif_${prayer.key.lowercase()}")
                        ) {
                            Icon(
                                imageVector = if (prayer.notifEnabled) Icons.Filled.NotificationsActive else Icons.Outlined.NotificationsOff,
                                contentDescription = "Notification",
                                tint = if (prayer.notifEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Expanded Prayer Information & Actions
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Structure: ${prayer.rakahs}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (prayer.key != "SUNRISE") {
                            TextButton(
                                onClick = onEditTime,
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Customize", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
