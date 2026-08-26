package com.example.ui.screens.settings

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AdhanAudioPlayer
import com.example.data.local.IslamicDataSource
import com.example.domain.calculator.CalculationMethod
import com.example.domain.calculator.Madhhab
import com.example.ui.MainViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    val settings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showMethodDialog by remember { mutableStateOf(false) }
    var showCityDialog by remember { mutableStateOf(false) }
    var showSourcesDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var isPlayingAdhanTest by remember { mutableStateOf(false) }

    // Profile form state
    var editName by remember(settings.profileName) { mutableStateOf(settings.profileName) }
    var editBio by remember(settings.profileBio) { mutableStateOf(settings.profileBio) }
    var editAvatar by remember(settings.profileAvatar) { mutableStateOf(settings.profileAvatar.ifEmpty { "👤" }) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Optional Profile Section
        item {
            SectionHeader(title = "User Profile (Optional)")
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("settings_profile_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (settings.hasCustomProfile && settings.profileAvatar.isNotBlank()) settings.profileAvatar else "👤",
                                fontSize = 24.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (settings.hasCustomProfile && settings.profileName.isNotBlank()) settings.profileName else "Guest User (No Profile)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (settings.hasCustomProfile && settings.profileBio.isNotBlank()) settings.profileBio else "Optional — all app features work offline",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = { showProfileDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("manage_profile_button")
                    ) {
                        Text(
                            text = if (settings.hasCustomProfile) "Edit" else "Create",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Location & Calculation Group
        item {
            SectionHeader(title = "Prayer Times & Local Calculations")
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("settings_calculation_card")
            ) {
                Column {
                    SettingRow(
                        title = "Location / City",
                        subtitle = "${settings.cityName}, ${settings.countryName} (${settings.latitude.toString().take(6)}, ${settings.longitude.toString().take(6)})",
                        icon = Icons.Filled.LocationOn,
                        onClick = { showCityDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingRow(
                        title = "Calculation Method",
                        subtitle = settings.calculationMethod.displayName,
                        icon = Icons.Filled.Calculate,
                        onClick = { showMethodDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingToggleRow(
                        title = "Hanafi Asr Time",
                        subtitle = if (settings.madhhab == Madhhab.HANAFI) "Hanafi (Shadow length 2x)" else "Standard Shafi'i/Maliki/Hanbali (Shadow length 1x)",
                        icon = Icons.Filled.AccessTime,
                        isChecked = settings.madhhab == Madhhab.HANAFI,
                        onCheckedChange = { isHanafi ->
                            coroutineScope.launch {
                                viewModel.preferencesRepository.updateMadhhab(if (isHanafi) Madhhab.HANAFI else Madhhab.STANDARD)
                            }
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // 12-Hour vs 24-Hour Time Format Setting
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("settings_time_format_section")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Time Format",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (settings.is24HourFormat) "24-Hour Clock (e.g. 17:45)" else "12-Hour Clock (e.g. 5:45 PM)",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 12-Hour Option Button
                            FilterChip(
                                selected = !settings.is24HourFormat,
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.preferencesRepository.update24HourFormat(false)
                                    }
                                },
                                label = {
                                    Text(
                                        "12-Hour (1:30 PM)",
                                        fontSize = 12.sp,
                                        fontWeight = if (!settings.is24HourFormat) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (!settings.is24HourFormat) {
                                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.weight(1f).testTag("time_format_12h_chip")
                            )

                            // 24-Hour Option Button
                            FilterChip(
                                selected = settings.is24HourFormat,
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.preferencesRepository.update24HourFormat(true)
                                    }
                                },
                                label = {
                                    Text(
                                        "24-Hour (13:30)",
                                        fontSize = 12.sp,
                                        fontWeight = if (settings.is24HourFormat) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                leadingIcon = if (settings.is24HourFormat) {
                                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                modifier = Modifier.weight(1f).testTag("time_format_24h_chip")
                            )
                        }
                    }
                }
            }
        }

        // Notification & Adhan Preferences
        item {
            SectionHeader(title = "Adhan & Prayer Alerts (All 5 Prayers)")
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("settings_adhan_card")
            ) {
                Column {
                    SettingToggleRow(
                        title = "Adhan Notifications Master",
                        subtitle = "Trigger alerts on lock screen & play Adhan audio",
                        icon = Icons.Filled.NotificationsActive,
                        isChecked = settings.notificationsEnabled,
                        onCheckedChange = { enabled ->
                            coroutineScope.launch {
                                viewModel.preferencesRepository.updatePrayerNotification("all", enabled)
                            }
                        }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Individual prayer toggles
                    val prayerList = listOf(
                        "Fajr" to settings.fajrNotification,
                        "Dhuhr" to settings.dhuhrNotification,
                        "Asr" to settings.asrNotification,
                        "Maghrib" to settings.maghribNotification,
                        "Isha" to settings.ishaNotification
                    )

                    Text("Individual Prayer Reminders:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    prayerList.forEach { (prayerName, isEnabled) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(prayerName, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { check ->
                                    coroutineScope.launch {
                                        viewModel.preferencesRepository.updatePrayerNotification(prayerName.lowercase(), check)
                                    }
                                }
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Adhan Audio Preview Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Test Adhan Sound", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Preview Adhan audio call to prayer", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                if (isPlayingAdhanTest) {
                                    AdhanAudioPlayer.stopAdhan()
                                    isPlayingAdhanTest = false
                                } else {
                                    AdhanAudioPlayer.playAdhanSound(context, settings.adhanSoundName, durationSeconds = 12)
                                    isPlayingAdhanTest = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlayingAdhanTest) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlayingAdhanTest) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isPlayingAdhanTest) "Stop" else "Play Test", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quran Script & Display Settings
        item {
            SectionHeader(title = "Quran Arabic Script & Display")
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("settings_quran_card")
            ) {
                Column {
                    Text("Arabic Quran Script Calligraphy:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    QuranFontOption.values().forEach { fontOption ->
                        val isSelected = settings.quranFontFamily.equals(fontOption.id, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.preferencesRepository.updateQuranFontFamily(fontOption.id)
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fontOption.displayName,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = fontOption.styleDescription,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "بِسْمِ اللَّهِ",
                                    fontFamily = fontOption.fontFamily,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Arabic Font Size: ${settings.arabicFontSize.toInt()}sp", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Slider(
                        value = settings.arabicFontSize,
                        onValueChange = { coroutineScope.launch { viewModel.preferencesRepository.updateArabicFontSize(it) } },
                        valueRange = 18f..40f,
                        steps = 10
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingToggleRow(
                        title = "Show English Translation",
                        subtitle = "Display Sahih International translation",
                        icon = Icons.Filled.Translate,
                        isChecked = settings.showTranslation,
                        onCheckedChange = { coroutineScope.launch { viewModel.preferencesRepository.updateShowTranslation(it) } }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingToggleRow(
                        title = "Show Transliteration",
                        subtitle = "Helpful pronunciation guide",
                        icon = Icons.Filled.Spellcheck,
                        isChecked = settings.showTransliteration,
                        onCheckedChange = { coroutineScope.launch { viewModel.preferencesRepository.updateShowTransliteration(it) } }
                    )
                }
            }
        }

        // About & Authentic Sources Reassurance
        item {
            SectionHeader(title = "Authenticity & Developer Info")
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("settings_about_card")
            ) {
                Column {
                    SettingRow(
                        title = "Islamic Content Sources & References",
                        subtitle = "Verified Uthmani Quran, Sahih Bukhari, Sahih Muslim, Hisn al-Muslim",
                        icon = Icons.Filled.Verified,
                        onClick = { showSourcesDialog = true }
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingRow(
                        title = "100% Offline & Private",
                        subtitle = "All calculations, prayers, and tracker data stay locally on your device",
                        icon = Icons.Filled.Security,
                        onClick = {}
                    )

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    SettingRow(
                        title = "About DeenMate & Developer",
                        subtitle = "DeenMate by Rauf • Version 2.0 M",
                        icon = Icons.Filled.Info,
                        onClick = { showAboutDialog = true }
                    )
                }
            }
        }
    }

    // Optional Profile Dialog
    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (settings.hasCustomProfile) "Edit Profile" else "Create Optional Profile", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text(
                        "Creating a profile is completely optional. You can use DeenMate without creating any profile.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Display Name") },
                        placeholder = { Text("e.g. Abdullah, Fatima") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Spiritual Goal / Bio") },
                        placeholder = { Text("e.g. Memorizing Surah Al-Kahf") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Select Avatar:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))

                    val avatars = listOf("👤", "🌙", "🕌", "⭐", "📖", "🤲", "🌿", "🕋")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatars.forEach { avatarEmoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (editAvatar == avatarEmoji) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { editAvatar = avatarEmoji },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(avatarEmoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.preferencesRepository.saveUserProfile(
                                name = editName.trim().ifEmpty { "Believer" },
                                bio = editBio.trim(),
                                avatar = editAvatar
                            )
                            showProfileDialog = false
                        }
                    }
                ) {
                    Text("Save Profile")
                }
            },
            dismissButton = {
                if (settings.hasCustomProfile) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.preferencesRepository.deleteUserProfile()
                                editName = ""
                                editBio = ""
                                editAvatar = "👤"
                                showProfileDialog = false
                            }
                        }
                    ) {
                        Text("Remove Profile", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    // About Developer Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Mosque, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("About DeenMate", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("DeenMate — Version 2.0 M", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("by Rauf", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("• Purpose: Built with care by Lead Developer Rauf to empower every Muslim with authentic, offline-first tools for daily worship, Quran recitation & multiple scripts, high-precision local prayer times, Qibla finding, and deen tracking.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• Local Calculation Engine: Solar astronomical algorithm with exact latitude, longitude, and local timezone awareness.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("May Allah accept this humble effort from Developer Rauf and make it beneficial for the Ummah.", fontSize = 12.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = MaterialTheme.colorScheme.primary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Sources Dialog
    if (showSourcesDialog) {
        AlertDialog(
            onDismissRequest = { showSourcesDialog = false },
            title = { Text("Authentic Islamic Sources", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("1. Holy Quran: Tanzil.net verified Uthmani Text & Sahih International English translation.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. Hadith: Sahih al-Bukhari & Sahih Muslim collections.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. Duas: Hisn al-Muslim (Fortress of the Muslim) by Sa'id bin Wahf Al-Qahtani.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("4. Prayer Calculations: Muslim World League, ISNA, Umm al-Qura, Egyptian General Authority of Survey, University of Islamic Sciences Karachi, and standard solar algorithms.", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSourcesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // City Selection Dialog
    if (showCityDialog) {
        AlertDialog(
            onDismissRequest = { showCityDialog = false },
            title = { Text("Select Your Location", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(IslamicDataSource.POPULAR_CITIES) { city ->
                        val isSelected = city.name == settings.cityName
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.selectCity(city)
                                        showCityDialog = false
                                    }
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = city.name,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(text = city.country, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (isSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCityDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Calculation Method Dialog
    if (showMethodDialog) {
        AlertDialog(
            onDismissRequest = { showMethodDialog = false },
            title = { Text("Calculation Method", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(CalculationMethod.values()) { method ->
                        val isSelected = method == settings.calculationMethod
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    coroutineScope.launch {
                                        viewModel.preferencesRepository.updateCalculationMethod(method)
                                        showMethodDialog = false
                                    }
                                },
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = method.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMethodDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}
