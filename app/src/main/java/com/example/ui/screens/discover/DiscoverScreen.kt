package com.example.ui.screens.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicDataSource
import com.example.data.model.DuaAzkar
import com.example.data.model.Hadith
import com.example.domain.calculator.HijriCalendarCalculator
import com.example.ui.MainViewModel
import com.example.ui.components.CategoryChip
import com.example.ui.components.IslamicBannerCard
import com.example.ui.components.SectionHeader
import com.example.ui.navigation.DiscoverTab
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiscoverScreen(
    viewModel: MainViewModel,
    initialTab: DiscoverTab = DiscoverTab.QIBLA
) {
    var currentTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(initialTab) {
        currentTab = initialTab
    }

    // Start compass sensor when Qibla tab is active
    DisposableEffect(currentTab) {
        if (currentTab == DiscoverTab.QIBLA) {
            viewModel.startCompass()
        }
        onDispose {
            if (currentTab == DiscoverTab.QIBLA) {
                viewModel.stopCompass()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab Horizontal Scroll Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(DiscoverTab.values()) { tab ->
                    CategoryChip(
                        title = tab.title,
                        isSelected = currentTab == tab,
                        onClick = { currentTab = tab }
                    )
                }
            }
        }

        // Active Tab View Content
        Box(modifier = Modifier.weight(1f)) {
            when (currentTab) {
                DiscoverTab.SEARCH -> SearchScreen(viewModel = viewModel)
                DiscoverTab.QIBLA -> QiblaFinderView(viewModel = viewModel)
                DiscoverTab.TASBEEH -> TasbeehView(viewModel = viewModel)
                DiscoverTab.HADITH -> HadithView(viewModel = viewModel)
                DiscoverTab.DUAS -> DuasAndAzkarView(viewModel = viewModel)
                DiscoverTab.CALENDAR -> HijriCalendarView(viewModel = viewModel)
                DiscoverTab.RAMADAN -> RamadanModeView(viewModel = viewModel)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. QIBLA FINDER VIEW
// -------------------------------------------------------------
@Composable
fun QiblaFinderView(viewModel: MainViewModel) {
    val qiblaState by viewModel.qiblaUiState.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    // Smooth rotation animation with spring damping
    val animatedCompassRotation by animateFloatAsState(
        targetValue = -qiblaState.deviceAzimuth,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "compass_rotation"
    )

    // Haptic feedback trigger when alignment state becomes true
    LaunchedEffect(qiblaState.isAlignedWithKaaba) {
        if (qiblaState.isAlignedWithKaaba) {
            viewModel.triggerHaptic(80L)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IslamicBannerCard(
                title = "Kaaba Direction: ${qiblaState.qiblaBearing.toInt()}°",
                subtitle = "${String.format(Locale.US, "%,d", qiblaState.distanceKm.toInt())} km from ${settings.cityName}",
                arabicGreeting = "فَوَلِّ وَجْهَكَ شَطْرَ الْمَسْجِدِ الْحَرَامِ",
                icon = Icons.Filled.Explore
            )
        }

        item {
            // Compass Visual
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(
                        if (qiblaState.isAlignedWithKaaba) EmeraldPrimary.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .testTag("qibla_compass_view"),
                contentAlignment = Alignment.Center
            ) {
                // Compass Dial
                Canvas(
                    modifier = Modifier
                        .size(280.dp)
                        .rotate(animatedCompassRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 16.dp.toPx()

                    // Draw outer circle
                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        radius = radius,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    // Draw Cardinal Ticks
                    for (i in 0 until 360 step 15) {
                        val angleRad = Math.toRadians(i.toDouble() - 90)
                        val isMajor = i % 90 == 0
                        val tickLen = if (isMajor) 14.dp.toPx() else 6.dp.toPx()

                        val startX = (center.x + (radius - tickLen) * cos(angleRad)).toFloat()
                        val startY = (center.y + (radius - tickLen) * sin(angleRad)).toFloat()
                        val endX = (center.x + radius * cos(angleRad)).toFloat()
                        val endY = (center.y + radius * sin(angleRad)).toFloat()

                        drawLine(
                            color = if (isMajor) EmeraldPrimary else Color.Gray.copy(alpha = 0.5f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isMajor) 3.dp.toPx() else 1.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Draw Qibla Pointer to Kaaba on the dial
                    val qiblaRad = Math.toRadians(qiblaState.qiblaBearing.toDouble() - 90)
                    val qiblaX = (center.x + (radius - 30.dp.toPx()) * cos(qiblaRad)).toFloat()
                    val qiblaY = (center.y + (radius - 30.dp.toPx()) * sin(qiblaRad)).toFloat()

                    drawCircle(
                        color = GoldAccentDark,
                        radius = 12.dp.toPx(),
                        center = Offset(qiblaX, qiblaY)
                    )
                }

                // Kaaba Icon in the Center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mosque,
                        contentDescription = "Kaaba",
                        tint = if (qiblaState.isAlignedWithKaaba) EmeraldPrimary else GoldAccentDark,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (qiblaState.isAlignedWithKaaba) "ALIGNED WITH KAABA" else "${qiblaState.deviceAzimuth.toInt()}°",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (qiblaState.isAlignedWithKaaba) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            // Feedback Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (qiblaState.isAlignedWithKaaba) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (qiblaState.isAlignedWithKaaba) Icons.Filled.CheckCircle else Icons.Filled.Info,
                        contentDescription = null,
                        tint = if (qiblaState.isAlignedWithKaaba) EmeraldPrimary else MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (qiblaState.isAlignedWithKaaba) "Facing Kaaba Perfectly!" else "Rotate your phone until the indicator turns green",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Calibrate by moving device in a figure-8 motion if needed.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. DIGITAL TASBEEH VIEW
// -------------------------------------------------------------
@Composable
fun TasbeehView(viewModel: MainViewModel) {
    val tasbeehState by viewModel.tasbeehUiState.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showPresetDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IslamicBannerCard(
                title = tasbeehState.selectedDhikr,
                subtitle = "Today's Total: ${tasbeehState.todayTotal} dhikr recited",
                arabicGreeting = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                icon = Icons.Filled.TouchApp
            )
        }

        // Target Selector & Dhikr Picker Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showPresetDialog = true },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Dhikr", fontSize = 12.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val targets = listOf(33, 99, 100, 0)
                    targets.forEach { t ->
                        val isSelected = tasbeehState.targetCount == t
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setTasbeehTarget(t) },
                            label = { Text(if (t == 0) "∞" else "$t", fontSize = 11.sp) },
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }
        }

        // Big Tap Circle
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .clickable { viewModel.incrementTasbeeh() }
                    .testTag("tasbeeh_tap_button"),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${tasbeehState.currentCount}",
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = if (tasbeehState.targetCount > 0) "Target: ${tasbeehState.targetCount}" else "Free Count",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TAP TO COUNT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Action Controls (Reset, Vibration)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { viewModel.resetTasbeeh() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reset")
                }

                Spacer(modifier = Modifier.width(16.dp))

                FilledTonalButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.preferencesRepository.updateTasbeehVibration(!settings.vibrationOnTasbeeh)
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (settings.vibrationOnTasbeeh) Icons.Filled.Vibration else Icons.Outlined.DoNotDisturbOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (settings.vibrationOnTasbeeh) "Vibration ON" else "Vibration OFF")
                }
            }
        }
    }

    // Preset Selection Dialog
    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = { Text("Select Dhikr / Tasbeeh", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    items(IslamicDataSource.PRESET_TASBEEH) { (title, desc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTasbeehDhikr(title)
                                    showPresetDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (tasbeehState.selectedDhikr == title) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPresetDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 3. HADITH VIEW
// -------------------------------------------------------------
@Composable
fun HadithView(viewModel: MainViewModel) {
    val selectedCategory by viewModel.selectedHadithCategory.collectAsState()
    val searchQuery by viewModel.hadithSearchQuery.collectAsState()
    val bookmarks by viewModel.allBookmarks.collectAsState()

    val categories = listOf("All", "Faith", "Prayer", "Fasting", "Charity", "Manners", "Dua", "Patience")

    val filteredHadiths = remember(selectedCategory, searchQuery) {
        IslamicDataSource.HADITHS.filter { h ->
            val matchesCat = selectedCategory == "All" || h.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    h.englishTranslation.contains(searchQuery, ignoreCase = true) ||
                    h.narrator.contains(searchQuery, ignoreCase = true) ||
                    h.collection.contains(searchQuery, ignoreCase = true) ||
                    h.chapterTitle.contains(searchQuery, ignoreCase = true)
            matchesCat && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateHadithSearchQuery(it) },
                placeholder = { Text("Search Sahih Hadiths...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateHadithSearchQuery("") }) {
                            Icon(Icons.Filled.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Categories Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    CategoryChip(
                        title = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { viewModel.selectHadithCategory(cat) }
                    )
                }
            }
        }

        items(filteredHadiths, key = { it.id }) { hadith ->
            val isBookmarked = bookmarks.any { it.id == hadith.id }
            HadithCardItem(
                hadith = hadith,
                isBookmarked = isBookmarked,
                onToggleBookmark = {
                    viewModel.toggleBookmark(
                        type = "HADITH",
                        id = hadith.id,
                        title = hadith.chapterTitle,
                        subtitle = "${hadith.collection} • ${hadith.hadithNumber}",
                        arSnippet = hadith.arabicText.take(60),
                        enSnippet = hadith.englishTranslation.take(60),
                        destData = hadith.id
                    )
                }
            )
        }
    }
}

@Composable
fun HadithCardItem(
    hadith: Hadith,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().testTag("hadith_card_${hadith.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = hadith.grade,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${hadith.collection} • ${hadith.hadithNumber}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) GoldAccentDark else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = hadith.chapterTitle, fontWeight = FontWeight.Bold, fontSize = 15.sp)

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = hadith.arabicText,
                fontSize = 18.sp,
                lineHeight = 30.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = hadith.englishTranslation,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Narrated by: ${hadith.narrator} (${hadith.bookName})",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// -------------------------------------------------------------
// 4. DUAS & AZKAR VIEW
// -------------------------------------------------------------
@Composable
fun DuasAndAzkarView(viewModel: MainViewModel) {
    val selectedCategory by viewModel.selectedDuaCategory.collectAsState()
    val bookmarks by viewModel.allBookmarks.collectAsState()

    val categories = listOf("Morning", "Evening", "After Salah", "Sleeping", "Waking Up", "Travel", "Forgiveness", "Daily")

    val filteredDuas = remember(selectedCategory) {
        IslamicDataSource.DUAS_AND_AZKAR.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(categories) { cat ->
                    CategoryChip(
                        title = cat,
                        isSelected = selectedCategory == cat,
                        onClick = { viewModel.selectDuaCategory(cat) }
                    )
                }
            }
        }

        items(filteredDuas, key = { it.id }) { dua ->
            var currentRepeat by remember { mutableStateOf(0) }
            val isBookmarked = bookmarks.any { it.id == dua.id }

            Card(
                modifier = Modifier.fillMaxWidth().testTag("dua_card_${dua.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dua.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            viewModel.toggleBookmark(
                                type = "DUA",
                                id = dua.id,
                                title = dua.title,
                                subtitle = dua.reference,
                                arSnippet = dua.arabicText.take(60),
                                enSnippet = dua.englishTranslation.take(60),
                                destData = dua.id
                            )
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isBookmarked) GoldAccentDark else MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dua.arabicText,
                        fontSize = 19.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (dua.transliteration.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dua.transliteration,
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dua.englishTranslation,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (dua.benefit.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Virtue: ${dua.benefit}",
                            fontSize = 11.sp,
                            color = GoldAccentLight,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ref: ${dua.reference}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        // Repeat Counter Button
                        FilledTonalButton(
                            onClick = {
                                if (currentRepeat < dua.targetCount) {
                                    currentRepeat++
                                } else {
                                    currentRepeat = 0
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (currentRepeat == dua.targetCount) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = if (currentRepeat >= dua.targetCount) "Completed (${dua.targetCount}x) ✓" else "Count: $currentRepeat / ${dua.targetCount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. HIJRI CALENDAR VIEW
// -------------------------------------------------------------
@Composable
fun HijriCalendarView(viewModel: MainViewModel) {
    val homeState by viewModel.homeUiState.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val hijriDate = homeState.hijriDate ?: return

    val islamicEvents = listOf(
        Triple("Ramadan Starts", "1 Ramadan", "Holy month of fasting and revelation of Qur'an"),
        Triple("Laylat al-Qadr", "27 Ramadan", "The Night of Decree and Power (better than 1,000 months)"),
        Triple("Eid al-Fitr", "1 Shawwal", "Islamic Celebration marking the end of Ramadan"),
        Triple("Day of Arafah", "9 Dhu al-Hijjah", "Pinnacle of Hajj pilgrimage"),
        Triple("Eid al-Adha", "10 Dhu al-Hijjah", "Feast of Sacrifice"),
        Triple("Islamic New Year", "1 Muharram", "Beginning of Hijri year"),
        Triple("Day of Ashura", "10 Muharram", "Fasting of Ashura expiating sins of past year")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IslamicBannerCard(
                title = "${hijriDate.formatShort()} (${hijriDate.formatArabic()})",
                subtitle = "Umm al-Qura Astronomical Hijri Calendar",
                arabicGreeting = "اللَّهُمَّ أَهِلَّهُ عَلَيْنَا بِالْأَمْنِ وَالْإِيمَانِ",
                icon = Icons.Filled.CalendarMonth
            )
        }

        // Hijri Adjustment Control Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Moon Sighting Adjustment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Current offset: ${if (settings.hijriAdjustment >= 0) "+${settings.hijriAdjustment}" else "${settings.hijriAdjustment}"} days", fontSize = 12.sp)
                    }

                    Row {
                        IconButton(onClick = {
                            coroutineScope.launch { viewModel.preferencesRepository.updateHijriAdjustment((settings.hijriAdjustment - 1).coerceAtLeast(-2)) }
                        }) {
                            Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease")
                        }
                        IconButton(onClick = {
                            coroutineScope.launch { viewModel.preferencesRepository.updateHijriAdjustment((settings.hijriAdjustment + 1).coerceAtMost(2)) }
                        }) {
                            Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase")
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Blessed Islamic Holy Days & Events")
        }

        items(islamicEvents) { (title, date, desc) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccentDark, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(date, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(desc, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. RAMADAN MODE VIEW
// -------------------------------------------------------------
@Composable
fun RamadanModeView(viewModel: MainViewModel) {
    val homeState by viewModel.homeUiState.collectAsState()
    val todayRecord = homeState.todayDeenRecord
    val result = homeState.prayerTimes
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IslamicBannerCard(
                title = "Ramadan Mubarak Mode",
                subtitle = "Sehri Ends at Fajr (${result?.let { timeFormat.format(it.fajr) } ?: "--:--"}) • Iftar at Maghrib (${result?.let { timeFormat.format(it.maghrib) } ?: "--:--"})",
                arabicGreeting = "شَهْرُ رَمَضَانَ الَّذِي أُنزِلَ فِيهِ الْقُرْآنُ",
                icon = Icons.Filled.NightsStay
            )
        }

        // Fasting Check-off
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Fasting Status", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    val statuses = listOf("Fasting" to "FASTING", "Completed" to "COMPLETED", "Not Fasting" to "NONE")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        statuses.forEach { (label, status) ->
                            val isSelected = todayRecord?.fastingStatus == status
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateFastingStatus(status) },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Ramadan Worship Checklist
        item {
            SectionHeader(title = "Daily Ramadan Worship Checklist")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RamadanCheckItem("Taraweeh Prayer", todayRecord?.taraweehCompleted ?: false) { viewModel.togglePrayer("taraweeh") }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RamadanCheckItem("Tahajjud / Qiyam", todayRecord?.tahajjudCompleted ?: false) { viewModel.togglePrayer("tahajjud") }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RamadanCheckItem("Morning Azkar", todayRecord?.morningAzkarCompleted ?: false) { viewModel.togglePrayer("morning_azkar") }
                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                    RamadanCheckItem("Evening Azkar", todayRecord?.eveningAzkarCompleted ?: false) { viewModel.togglePrayer("evening_azkar") }
                }
            }
        }

        // Iftar Dua Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Dua for Breaking the Fast (Iftar)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Right,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun RamadanCheckItem(
    title: String,
    isChecked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Checkbox(checked = isChecked, onCheckedChange = { onToggle() })
    }
}

// -------------------------------------------------------------
// 7. GLOBAL SEARCH VIEW
// -------------------------------------------------------------
@Composable
fun GlobalSearchView(viewModel: MainViewModel) {
    val query by viewModel.globalSearchQuery.collectAsState()
    val filter by viewModel.globalSearchFilter.collectAsState()

    val quranResults = remember(query) {
        if (query.isBlank()) emptyList()
        else IslamicDataSource.SURAHS.filter {
            it.nameEnglish.contains(query, ignoreCase = true) ||
            it.englishTranslation.contains(query, ignoreCase = true) ||
            it.nameArabic.contains(query)
        }
    }

    val hadithResults = remember(query) {
        if (query.isBlank()) emptyList()
        else IslamicDataSource.HADITHS.filter {
            it.englishTranslation.contains(query, ignoreCase = true) ||
            it.chapterTitle.contains(query, ignoreCase = true)
        }
    }

    val duaResults = remember(query) {
        if (query.isBlank()) emptyList()
        else IslamicDataSource.DUAS_AND_AZKAR.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.englishTranslation.contains(query, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.updateGlobalSearchQuery(it) },
                placeholder = { Text("Search Quran, Hadith, Duas...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ALL", "QURAN", "HADITH", "DUA").forEach { f ->
                    CategoryChip(title = f, isSelected = filter == f, onClick = { viewModel.setGlobalSearchFilter(f) })
                }
            }
        }

        if (query.isNotBlank()) {
            if (filter == "ALL" || filter == "QURAN") {
                item { SectionHeader(title = "Quran Surahs (${quranResults.size})") }
                items(quranResults) { surah ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${surah.number}. ${surah.nameEnglish}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text(surah.nameArabic, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            if (filter == "ALL" || filter == "HADITH") {
                item { SectionHeader(title = "Hadiths (${hadithResults.size})") }
                items(hadithResults) { hadith ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(hadith.chapterTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(hadith.englishTranslation, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            if (filter == "ALL" || filter == "DUA") {
                item { SectionHeader(title = "Duas & Azkar (${duaResults.size})") }
                items(duaResults) { dua ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(dua.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(dua.englishTranslation, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
