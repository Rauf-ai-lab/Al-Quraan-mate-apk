package com.example.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.DuaAzkar
import com.example.data.model.Hadith
import com.example.domain.calculator.PrayerType
import com.example.ui.HomeUiState
import com.example.ui.MainViewModel
import com.example.ui.components.IslamicBannerCard
import com.example.ui.components.LiquidGlassCard
import com.example.ui.components.MakkahMadinahLiveCard
import com.example.ui.components.ProminentHijriHeroCard
import com.example.ui.components.SectionHeader
import com.example.ui.navigation.DiscoverTab
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToScreen: (String) -> Unit,
    onNavigateToDiscoverTab: (DiscoverTab) -> Unit,
    onOpenSurah: (Int) -> Unit
) {
    val uiState by viewModel.homeUiState.collectAsState()
    val is24Hour = uiState.userSettings.is24HourFormat
    val timeFormat = remember(is24Hour) {
        SimpleDateFormat(if (is24Hour) "HH:mm" else "hh:mm a", Locale.getDefault())
    }
    val shortTimeFormat = remember(is24Hour) {
        SimpleDateFormat(if (is24Hour) "HH:mm" else "h:mm a", Locale.getDefault())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Top Brand & Developer Identity Header: "DeenMate by Rauf"
        item {
            LiquidGlassCard(
                modifier = Modifier.fillMaxWidth().testTag("deenmate_top_brand_header")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "DeenMate",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "v2.0 M",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }
                        Text(
                            text = "by Rauf",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Optional Profile Quick Badge
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onNavigateToScreen(Screen.Settings.route) }
                            .testTag("home_profile_badge"),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                    ) {
                        if (uiState.userSettings.hasCustomProfile && uiState.userSettings.profileName.isNotBlank()) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = uiState.userSettings.profileAvatar.ifEmpty { "👤" },
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = uiState.userSettings.profileName.take(10),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.PersonOutline,
                                    contentDescription = "Optional Profile",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Profile",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Prominent Hijri & Gregorian Date Hero Card (java.time.chrono.HijrahDate API)
        item {
            ProminentHijriHeroCard(
                hijriDate = uiState.hijriDate,
                gregorianDateText = uiState.gregorianDateText,
                cityName = uiState.userSettings.cityName,
                hijriAdjustment = uiState.userSettings.hijriAdjustment,
                onViewCalendar = { onNavigateToDiscoverTab(DiscoverTab.CALENDAR) },
                onAdjustOffset = { newOffset ->
                    viewModel.updateHijriAdjustment(newOffset)
                }
            )
        }

        // Makkah & Madinah Live Visual
        item {
            MakkahMadinahLiveCard()
        }

        // Search Bar Tile on Dashboard
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigateToScreen(Screen.Search.route) }
                    .testTag("dashboard_search_bar"),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Search Quran, Hadith & Duas...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tap to search or ask any Islamic topic",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "Search",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }
        }

        // 2. Next Prayer Countdown Card
        item {
            NextPrayerCountdownCard(
                uiState = uiState,
                timeFormat = timeFormat,
                onViewPrayerSchedule = { onNavigateToScreen(Screen.Prayer.route) }
            )
        }

        // 3. Quick Action Buttons
        item {
            QuickActionGrid(
                onNavigateToScreen = onNavigateToScreen,
                onNavigateToDiscoverTab = onNavigateToDiscoverTab
            )
        }

        // 4. Today's Deen Progress Tracker Card
        item {
            TodayDeenProgressCard(
                score = uiState.deenScore,
                uiState = uiState,
                onTogglePrayer = { viewModel.togglePrayer(it) },
                onViewFullTracker = { onNavigateToScreen(Screen.Tracker.route) }
            )
        }

        // 5. Daily Quran Ayah
        item {
            DailyAyahCard(
                ayah = uiState.dailyAyah,
                surahName = uiState.dailyAyahSurahName,
                quranFontFamily = uiState.userSettings.quranFontFamily,
                arabicFontSize = uiState.userSettings.arabicFontSize,
                onReadQuran = { onOpenSurah(2) },
                onBookmark = {
                    uiState.dailyAyah?.let { ayah ->
                        viewModel.toggleBookmark(
                            type = "QURAN",
                            id = "quran_${ayah.surahNumber}_${ayah.numberInSurah}",
                            title = "Surah Al-Baqarah Ayah 255",
                            subtitle = "Ayat al-Kursi",
                            arSnippet = ayah.arabicText.take(60),
                            enSnippet = ayah.englishTranslation.take(60),
                            destData = "${ayah.surahNumber}"
                        )
                    }
                }
            )
        }

        // 6. Daily Hadith Card
        item {
            DailyHadithCard(
                hadith = uiState.dailyHadith,
                onViewHadiths = { onNavigateToDiscoverTab(DiscoverTab.HADITH) }
            )
        }

        // 7. Daily Dua Card (Hisn al-Muslim)
        item {
            DailyDuaCard(
                dua = uiState.dailyDua,
                onViewDuas = { onNavigateToDiscoverTab(DiscoverTab.DUAS) }
            )
        }

        // 8. Developer & App Identity Footer Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("developer_footer_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mosque,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "DeenMate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Crafted with devotion by Lead Developer Rauf",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% Offline Core • Authentic Uthmani Quran • High-Precision Local Prayer Engine",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun NextPrayerCountdownCard(
    uiState: HomeUiState,
    timeFormat: SimpleDateFormat,
    onViewPrayerSchedule: () -> Unit
) {
    val result = uiState.prayerTimes ?: return

    val totalSec = (result.timeRemainingMillis / 1000).coerceAtLeast(0)
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    val formattedCountdown = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onViewPrayerSchedule() }
            .testTag("next_prayer_countdown_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GoldAccentLight)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Next Prayer: ${result.currentOrNextPrayer.displayName}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = result.currentOrNextPrayer.arabicName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formattedCountdown,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Time at ${timeFormat.format(result.nextPrayerTime)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                FilledTonalButton(
                    onClick = onViewPrayerSchedule,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("All Times", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Mini 5 Prayers Row
            val prayerItems = listOf(
                "Fajr" to (result.fajr to (result.currentOrNextPrayer == PrayerType.FAJR)),
                "Dhuhr" to (result.dhuhr to (result.currentOrNextPrayer == PrayerType.DHUHR)),
                "Asr" to (result.asr to (result.currentOrNextPrayer == PrayerType.ASR)),
                "Maghrib" to (result.maghrib to (result.currentOrNextPrayer == PrayerType.MAGHRIB)),
                "Isha" to (result.isha to (result.currentOrNextPrayer == PrayerType.ISHA))
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                prayerItems.forEach { (name, pair) ->
                    val (time, isNext) = pair
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isNext) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Medium,
                            color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = timeFormat.format(time),
                            fontSize = 11.sp,
                            fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                            color = if (isNext) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionGrid(
    onNavigateToScreen: (String) -> Unit,
    onNavigateToDiscoverTab: (DiscoverTab) -> Unit
) {
    Column {
        SectionHeader(title = "Quick Shortcuts")

        val actions = listOf(
            Triple("Search", Icons.Filled.Search) { onNavigateToScreen(Screen.Search.route) },
            Triple("Holy Quran", Icons.Filled.MenuBook) { onNavigateToScreen(Screen.Quran.route) },
            Triple("Qibla Finder", Icons.Filled.Explore) { onNavigateToDiscoverTab(DiscoverTab.QIBLA) },
            Triple("Tasbeeh", Icons.Filled.TouchApp) { onNavigateToDiscoverTab(DiscoverTab.TASBEEH) },
            Triple("Duas & Azkar", Icons.Filled.Favorite) { onNavigateToDiscoverTab(DiscoverTab.DUAS) },
            Triple("Hadith", Icons.Filled.FormatQuote) { onNavigateToDiscoverTab(DiscoverTab.HADITH) },
            Triple("Hijri Dates", Icons.Filled.CalendarMonth) { onNavigateToDiscoverTab(DiscoverTab.CALENDAR) },
            Triple("Ramadan", Icons.Filled.NightsStay) { onNavigateToDiscoverTab(DiscoverTab.RAMADAN) }
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(actions) { (title, icon, action) ->
                Surface(
                    modifier = Modifier
                        .width(86.dp)
                        .height(92.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { action() }
                        .testTag("quick_action_${title.replace(" ", "_").lowercase()}"),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    shadowElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = title,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayDeenProgressCard(
    score: Int,
    uiState: HomeUiState,
    onTogglePrayer: (String) -> Unit,
    onViewFullTracker: () -> Unit
) {
    val rec = uiState.todayDeenRecord

    Card(
        modifier = Modifier.fillMaxWidth().testTag("today_deen_progress_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Today's Deen Journey",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$score% Completed today",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                TextButton(onClick = onViewFullTracker) {
                    Text("Details", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Quick Prayer Check-off:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val prayers = listOf(
                "Fajr" to (rec?.fajrCompleted ?: false),
                "Dhuhr" to (rec?.dhuhrCompleted ?: false),
                "Asr" to (rec?.asrCompleted ?: false),
                "Maghrib" to (rec?.maghribCompleted ?: false),
                "Isha" to (rec?.ishaCompleted ?: false)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                prayers.forEach { (name, isDone) ->
                    FilterChip(
                        selected = isDone,
                        onClick = { onTogglePrayer(name) },
                        label = { Text(name, fontSize = 11.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isDone) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isDone) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyAyahCard(
    ayah: Ayah?,
    surahName: String,
    quranFontFamily: String = "Uthmani",
    arabicFontSize: Float = 24f,
    onReadQuran: () -> Unit,
    onBookmark: () -> Unit
) {
    if (ayah == null) return

    Card(
        modifier = Modifier.fillMaxWidth().testTag("daily_ayah_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = GoldAccentLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Quran Ayah",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = surahName,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text with selected font
            Text(
                text = ayah.arabicText,
                fontFamily = resolveQuranFontFamily(quranFontFamily),
                fontSize = (arabicFontSize * 0.9f).sp,
                lineHeight = (arabicFontSize * 1.55f).sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Translation
            Text(
                text = "\"${ayah.englishTranslation}\"",
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBookmark) {
                    Icon(imageVector = Icons.Outlined.BookmarkAdd, contentDescription = "Bookmark", tint = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = onReadQuran) {
                    Text("Read in Quran", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DailyHadithCard(
    hadith: Hadith?,
    onViewHadiths: () -> Unit
) {
    if (hadith == null) return

    Card(
        modifier = Modifier.fillMaxWidth().testTag("daily_hadith_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.FormatQuote,
                        contentDescription = null,
                        tint = EmeraldSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Hadith",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "${hadith.collection} • ${hadith.hadithNumber}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = hadith.arabicText,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hadith.englishTranslation,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Narrated by ${hadith.narrator}",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                TextButton(onClick = onViewHadiths) {
                    Text("More Hadiths", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun DailyDuaCard(
    dua: DuaAzkar?,
    onViewDuas: () -> Unit
) {
    if (dua == null) return

    Card(
        modifier = Modifier.fillMaxWidth().testTag("daily_dua_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Daily Dua & Dhikr",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = dua.reference,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = dua.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dua.arabicText,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = dua.englishTranslation,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (dua.benefit.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Benefit: ${dua.benefit}",
                    fontSize = 11.sp,
                    color = GoldAccentLight,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onViewDuas) {
                    Text("All Duas & Azkar", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
