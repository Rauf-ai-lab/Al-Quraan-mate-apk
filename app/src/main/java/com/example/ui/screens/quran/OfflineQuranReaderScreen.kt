package com.example.ui.screens.quran

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicDataSource
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.MainViewModel
import com.example.ui.components.LiquidGlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineQuranReaderScreen(
    viewModel: MainViewModel,
    initialSurah: Surah? = null,
    initialJuzNumber: Int? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var currentSurah by remember {
        mutableStateOf(
            initialSurah ?: if (initialJuzNumber != null) {
                IslamicDataSource.SURAHS.find { it.juzNumber == initialJuzNumber } ?: IslamicDataSource.SURAHS[0]
            } else {
                IslamicDataSource.SURAHS[0]
            }
        )
    }

    var arabicFontSize by remember { mutableStateOf(26) }
    var showTranslation by remember { mutableStateOf(true) }
    var showTransliteration by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val ayahs = remember(currentSurah.number) {
        IslamicDataSource.getAyahsForSurah(currentSurah)
    }

    val bookmarks by viewModel.allBookmarks.collectAsState()
    val listState = rememberLazyListState()

    // Save reading progress whenever Surah opens
    LaunchedEffect(currentSurah.number) {
        viewModel.saveOfflineProgress(currentSurah.number, 1, currentSurah.startPage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Surah ${currentSurah.nameEnglish} • ${currentSurah.nameArabic}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "${currentSurah.revelationType} • ${currentSurah.numberOfAyahs} Verses • Juz ${currentSurah.juzNumber}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("offline_reader_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("offline_reader_settings_button")
                    ) {
                        Icon(Icons.Outlined.FormatSize, contentDescription = "Font Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Surah Navigation Footer
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (currentSurah.number > 1) {
                                val prev = IslamicDataSource.SURAHS.find { it.number == currentSurah.number - 1 }
                                if (prev != null) currentSurah = prev
                            }
                        },
                        enabled = currentSurah.number > 1,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.testTag("offline_prev_surah_btn")
                    ) {
                        Icon(Icons.Filled.ChevronLeft, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Previous")
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${currentSurah.number} / 114",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (currentSurah.number < 114) {
                                val next = IslamicDataSource.SURAHS.find { it.number == currentSurah.number + 1 }
                                if (next != null) currentSurah = next
                            }
                        },
                        enabled = currentSurah.number < 114,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary),
                        modifier = Modifier.testTag("offline_next_surah_btn")
                    ) {
                        Text("Next")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Surah Header Banner (Bismillah)
            item {
                LiquidGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offline_surah_header_banner"),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "سُورَةُ ${currentSurah.nameArabic}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccentDark,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Surah ${currentSurah.nameEnglish} • ${currentSurah.englishTranslation}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth(0.6f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        if (currentSurah.number != 9) { // At-Tawbah does not begin with Bismillah
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Ayahs List
            itemsIndexed(ayahs, key = { _, ayah -> "${currentSurah.number}_${ayah.numberInSurah}" }) { index, ayah ->
                val isBookmarked = bookmarks.any { it.type == "QURAN" && it.title.contains("Surah ${currentSurah.nameEnglish}") && it.subtitle.contains("Ayah ${ayah.numberInSurah}") }

                OfflineAyahItem(
                    ayah = ayah,
                    surah = currentSurah,
                    arabicFontSize = arabicFontSize,
                    showTranslation = showTranslation,
                    showTransliteration = showTransliteration,
                    isBookmarked = isBookmarked,
                    onBookmarkToggle = {
                        viewModel.toggleBookmark(
                            type = "QURAN",
                            id = "ayah_${currentSurah.number}_${ayah.numberInSurah}",
                            title = "Surah ${currentSurah.nameEnglish} (${currentSurah.nameArabic})",
                            subtitle = "Ayah ${ayah.numberInSurah}",
                            arSnippet = ayah.arabicText,
                            enSnippet = ayah.englishTranslation,
                            destData = "${currentSurah.number}"
                        )
                        Toast.makeText(context, if (isBookmarked) "Bookmark removed" else "Ayah bookmarked offline", Toast.LENGTH_SHORT).show()
                    },
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Quran Ayah", "${ayah.arabicText}\n\n${ayah.englishTranslation}\n(Surah ${currentSurah.nameEnglish} : ${ayah.numberInSurah})")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Ayah copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onMarkLastRead = {
                        viewModel.saveOfflineProgress(currentSurah.number, ayah.numberInSurah, ayah.page)
                        Toast.makeText(context, "Saved as last read (Ayah ${ayah.numberInSurah})", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Font & Display Settings Bottom Sheet
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Offline Reading Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Arabic Font Size Slider
                Text(
                    text = "Arabic Font Size: ${arabicFontSize}sp",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = arabicFontSize.toFloat(),
                    onValueChange = { arabicFontSize = it.toInt() },
                    valueRange = 18f..38f,
                    steps = 10,
                    modifier = Modifier.testTag("offline_font_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Toggle Translation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Show English Translation", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = showTranslation,
                        onCheckedChange = { showTranslation = it },
                        modifier = Modifier.testTag("offline_toggle_translation")
                    )
                }

                // Toggle Transliteration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Show English Transliteration", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = showTransliteration,
                        onCheckedChange = { showTransliteration = it },
                        modifier = Modifier.testTag("offline_toggle_transliteration")
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { showSettingsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Apply & Close")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun OfflineAyahItem(
    ayah: Ayah,
    surah: Surah,
    arabicFontSize: Int,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    onCopy: () -> Unit,
    onMarkLastRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("offline_ayah_${ayah.numberInSurah}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Ayah Number Badge + Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${surah.number}:${ayah.numberInSurah}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onMarkLastRead,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Mark Last Read",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onBookmarkToggle,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Arabic Text
            Text(
                text = ayah.arabicText,
                fontSize = arabicFontSize.sp,
                lineHeight = (arabicFontSize * 1.7).sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )

            // Transliteration
            if (showTransliteration && ayah.transliteration.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.transliteration,
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Translation
            if (showTranslation) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = ayah.englishTranslation,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
