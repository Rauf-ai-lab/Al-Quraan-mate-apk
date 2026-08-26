package com.example.ui.screens.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.ReciterVoicePacks
import com.example.data.local.IslamicDataSource
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.MainViewModel
import com.example.ui.components.IslamicBannerCard
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahReaderScreen(
    initialSurah: Surah? = null,
    initialJuzNumber: Int? = null,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val ayahs by viewModel.selectedSurahAyahs.collectAsState()
    val selectedSurahState by viewModel.selectedSurah.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val bookmarks by viewModel.allBookmarks.collectAsState()
    val playbackState by viewModel.quranPlaybackState.collectAsState()

    val currentSurah = initialSurah ?: selectedSurahState ?: IslamicDataSource.SURAHS[0]

    var showFontDialog by remember { mutableStateOf(false) }
    var showReciterSheet by remember { mutableStateOf(false) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    LaunchedEffect(initialSurah, initialJuzNumber) {
        if (initialJuzNumber != null) {
            viewModel.selectJuz(initialJuzNumber)
        } else if (initialSurah != null) {
            viewModel.selectSurah(initialSurah)
        }
    }

    // Auto scroll when current playing ayah changes in strict sequence
    LaunchedEffect(playbackState.currentAyah?.surahNumber, playbackState.currentAyah?.numberInSurah) {
        val curAyah = playbackState.currentAyah
        if (curAyah != null) {
            val targetIndex = ayahs.indexOfFirst {
                it.surahNumber == curAyah.surahNumber && it.numberInSurah == curAyah.numberInSurah
            }
            if (targetIndex >= 0) {
                listState.animateScrollToItem((targetIndex + 1).coerceAtLeast(0))
            }
        }
    }

    val activePlayingSurah = playbackState.currentSurah
        ?: IslamicDataSource.SURAHS.find { it.number == playbackState.currentAyah?.surahNumber }
        ?: currentSurah

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        if (initialJuzNumber != null) {
                            Text(
                                text = "Juz $initialJuzNumber • ${currentSurah.nameEnglish} (${currentSurah.nameArabic})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${ayahs.size} Ayahs in Juz $initialJuzNumber",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text = "Surah ${currentSurah.nameEnglish} (${currentSurah.nameArabic})",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentSurah.revelationType} • ${currentSurah.numberOfAyahs} Ayahs • Juz ${currentSurah.juzNumber}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("surah_reader_back_button")) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showReciterSheet = true },
                        modifier = Modifier.testTag("select_reciter_button")
                    ) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Select Reciter", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = { showFontDialog = true }) {
                        Icon(Icons.Filled.FormatSize, contentDescription = "Font Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Enhanced Quran Audio Player Floating Bar with Live Text & Translation Sync
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quran_audio_player_bar"),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 12.dp,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Error message banner with retry
                    AnimatedVisibility(visible = playbackState.errorMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = playbackState.errorMessage ?: "Audio playback error",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(
                                    onClick = {
                                        val curAyah = playbackState.currentAyah ?: ayahs.firstOrNull()
                                        if (curAyah != null) {
                                            viewModel.playQuranAyah(activePlayingSurah, curAyah)
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Retry", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Currently Playing Ayah Live Text Preview (Synced in Real-time)
                    playbackState.currentAyah?.let { liveAyah ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Now Playing: Surah ${activePlayingSurah.nameEnglish} • Ayah ${liveAyah.numberInSurah}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (playbackState.isFromAi) {
                                        Text(
                                            text = "✨ AI Tajweed Voice",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = GoldAccentDark
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = liveAyah.arabicText,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Right,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (settings.showTranslation && liveAyah.englishTranslation.isNotBlank()) {
                                    Text(
                                        text = liveAyah.englishTranslation,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Progress Slider
                    if (playbackState.isPlaying || playbackState.isPaused || playbackState.durationMs > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTimeMs(playbackState.currentPositionMs),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = playbackState.playbackProgress,
                                onValueChange = { viewModel.seekQuranAudio(it) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(24.dp)
                                    .padding(horizontal = 8.dp)
                            )
                            Text(
                                text = formatTimeMs(playbackState.durationMs),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Player Control Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Reciter Info & Switcher Button
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showReciterSheet = true }
                                .padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (playbackState.isPlaying) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (playbackState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Audiotrack,
                                        contentDescription = null,
                                        tint = if (playbackState.isPlaying) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (playbackState.currentAyah != null)
                                        "Ayah ${playbackState.currentAyah?.numberInSurah} • Surah ${activePlayingSurah.nameEnglish}"
                                    else "Surah ${currentSurah.nameEnglish}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = playbackState.selectedReciter.name,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Playback Speed Button
                        Box {
                            TextButton(
                                onClick = { showSpeedMenu = true },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${playbackState.playbackSpeed}x",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = showSpeedMenu,
                                onDismissRequest = { showSpeedMenu = false }
                            ) {
                                listOf(0.75f, 1.0f, 1.25f, 1.5f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = { Text("${speed}x", fontWeight = if (playbackState.playbackSpeed == speed) FontWeight.Bold else FontWeight.Normal) },
                                        onClick = {
                                            viewModel.setQuranPlaybackSpeed(speed)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Previous Ayah Button
                        IconButton(
                            onClick = { viewModel.previousQuranAyah() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("previous_ayah_button"),
                            enabled = playbackState.currentAyah != null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipPrevious,
                                contentDescription = "Previous Ayah",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Main Play / Pause Button
                        IconButton(
                            onClick = {
                                if (playbackState.currentAyah == null) {
                                    val first = ayahs.firstOrNull()
                                    if (first != null) {
                                        val parent = IslamicDataSource.SURAHS.find { it.number == first.surahNumber } ?: currentSurah
                                        viewModel.playQuranAyah(parent, first)
                                    }
                                } else {
                                    viewModel.toggleQuranPlayPause()
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .testTag("play_pause_button")
                        ) {
                            if (playbackState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        // Next Ayah Button
                        IconButton(
                            onClick = { viewModel.nextQuranAyah() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("next_ayah_button"),
                            enabled = playbackState.currentAyah != null
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SkipNext,
                                contentDescription = "Next Ayah",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Stop Button
                        IconButton(
                            onClick = { viewModel.stopQuranAudio() },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("stop_audio_button"),
                            enabled = playbackState.isPlaying || playbackState.isPaused
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Stop,
                                contentDescription = "Stop",
                                tint = if (playbackState.isPlaying || playbackState.isPaused) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Surah / Juz Info Header
            item {
                if (initialJuzNumber != null) {
                    IslamicBannerCard(
                        title = "الْجُزْءُ $initialJuzNumber",
                        subtitle = "Juz $initialJuzNumber • ${currentSurah.nameEnglish} (${currentSurah.nameArabic})",
                        arabicGreeting = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        icon = Icons.Filled.MenuBook
                    )
                } else {
                    IslamicBannerCard(
                        title = "سُورَةُ ${currentSurah.nameArabic}",
                        subtitle = "${currentSurah.nameEnglish} • ${currentSurah.englishTranslation}",
                        arabicGreeting = if (currentSurah.number != 9) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" else "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ",
                        icon = Icons.Filled.MenuBook
                    )
                }
            }

            // Ayahs list
            items(ayahs, key = { "${it.surahNumber}_${it.numberInSurah}" }) { ayah ->
                val isBookmarked = bookmarks.any { it.id == "quran_${ayah.surahNumber}_${ayah.numberInSurah}" }
                val isCurrentlyPlayingThisAyah = playbackState.currentAyah?.surahNumber == ayah.surahNumber &&
                        playbackState.currentAyah?.numberInSurah == ayah.numberInSurah

                val parentSurah = IslamicDataSource.SURAHS.find { it.number == ayah.surahNumber } ?: currentSurah

                AyahCard(
                    ayah = ayah,
                    arabicFontSize = settings.arabicFontSize,
                    quranFontFamily = settings.quranFontFamily,
                    showTranslation = settings.showTranslation,
                    showTransliteration = settings.showTransliteration,
                    isBookmarked = isBookmarked,
                    isCurrentlyPlaying = isCurrentlyPlayingThisAyah,
                    isPlaying = isCurrentlyPlayingThisAyah && playbackState.isPlaying,
                    isLoading = isCurrentlyPlayingThisAyah && playbackState.isLoading,
                    onPlayAyah = {
                        if (isCurrentlyPlayingThisAyah && playbackState.isPlaying) {
                            viewModel.toggleQuranPlayPause()
                        } else {
                            viewModel.playQuranAyah(parentSurah, ayah)
                        }
                    },
                    onToggleBookmark = {
                        viewModel.toggleBookmark(
                            type = "QURAN",
                            id = "quran_${ayah.surahNumber}_${ayah.numberInSurah}",
                            title = "Surah ${parentSurah.nameEnglish} : ${ayah.numberInSurah}",
                            subtitle = parentSurah.englishTranslation,
                            arSnippet = ayah.arabicText.take(60),
                            enSnippet = ayah.englishTranslation.take(60),
                            destData = "${parentSurah.number}"
                        )
                    },
                    onCopyText = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Ayah", "${ayah.arabicText}\n\n\"${ayah.englishTranslation}\"\n(Quran ${ayah.surahNumber}:${ayah.numberInSurah})")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Reciter Voice Packs Selection Modal Bottom Sheet
    if (showReciterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReciterSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.RecordVoiceOver,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Select Moulana / Reciter Voice",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "High-fidelity AI Tajweed voice synthesis & authentic recitation voice-packs.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))

                ReciterVoicePacks.RECITERS.forEach { reciter ->
                    val isSelected = playbackState.selectedReciter.id == reciter.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.selectReciterVoice(reciter)
                                showReciterSheet = false
                                Toast.makeText(context, "Selected Reciter: ${reciter.name}", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reciter.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = reciter.arabicName,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = reciter.styleDescription,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Font Size & View Settings Dialog
    if (showFontDialog) {
        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = { Text("Reading Preferences", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Arabic Quran Script",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

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

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Arabic Font Size: ${settings.arabicFontSize.toInt()}sp",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Slider(
                        value = settings.arabicFontSize,
                        onValueChange = { coroutineScope.launch { viewModel.preferencesRepository.updateArabicFontSize(it) } },
                        valueRange = 18f..40f,
                        steps = 10
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Show Translation", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = settings.showTranslation,
                            onCheckedChange = { coroutineScope.launch { viewModel.preferencesRepository.updateShowTranslation(it) } }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Show Transliteration", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = settings.showTransliteration,
                            onCheckedChange = { coroutineScope.launch { viewModel.preferencesRepository.updateShowTransliteration(it) } }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun AyahCard(
    ayah: Ayah,
    arabicFontSize: Float,
    quranFontFamily: String = "Uthmani",
    showTranslation: Boolean,
    showTransliteration: Boolean,
    isBookmarked: Boolean,
    isCurrentlyPlaying: Boolean,
    isPlaying: Boolean,
    isLoading: Boolean,
    onPlayAyah: () -> Unit,
    onToggleBookmark: () -> Unit,
    onCopyText: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(300),
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("ayah_card_${ayah.numberInSurah}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isCurrentlyPlaying) BorderStroke(1.5.dp, borderColor) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(if (isCurrentlyPlaying) 3.dp else 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Verse Number badge + Play Ayah + Copy + Bookmark Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ayah.numberInSurah}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentlyPlaying) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Dedicated Play Ayah Button
                    FilledTonalIconButton(
                        onClick = onPlayAyah,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("play_ayah_${ayah.numberInSurah}"),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = if (isCurrentlyPlaying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause Ayah" else "Play Ayah",
                                tint = if (isCurrentlyPlaying) Color.White else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onCopyText, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onToggleBookmark, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) GoldAccentDark else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic text with right alignment and selected Quran font family
            Text(
                text = ayah.arabicText,
                fontFamily = resolveQuranFontFamily(quranFontFamily),
                fontSize = arabicFontSize.sp,
                lineHeight = (arabicFontSize * 1.65f).sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            // Transliteration
            if (showTransliteration && ayah.transliteration.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = ayah.transliteration,
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Translation
            if (showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.englishTranslation,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimeMs(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
