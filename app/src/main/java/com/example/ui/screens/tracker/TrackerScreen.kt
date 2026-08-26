package com.example.ui.screens.tracker

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.IslamicBannerCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@Composable
fun TrackerScreen(
    viewModel: MainViewModel
) {
    val homeState by viewModel.homeUiState.collectAsState()
    val todayRecord = homeState.todayDeenRecord
    val recentRecords by viewModel.recentDeenRecords.collectAsState()
    val completedFasts by viewModel.completedFastsCount.collectAsState()

    val score = homeState.deenScore
    val animatedProgress by animateFloatAsState(targetValue = score / 100f, label = "score_progress")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Score Banner
        item {
            IslamicBannerCard(
                title = "Today's Deen Score: $score%",
                subtitle = "Building consistent Islamic habits day by day",
                arabicGreeting = "وَقُلِ اعْمَلُوا فَسَيَرَى اللَّهُ عَمَلَكُمْ",
                icon = Icons.Filled.TaskAlt
            )
        }

        // Circular Score Progress
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("deen_score_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 10.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score%",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = if (score == 100) "Masha'Allah!" else "In Progress",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Quran Time", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${todayRecord?.quranMinutes ?: 0} mins", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tasbeeh Count", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${todayRecord?.tasbeehCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Days Fasted", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$completedFasts days", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        // Obligatory 5 Prayers Checklist
        item {
            SectionHeader(title = "5 Daily Obligatory Prayers")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val prayers = listOf(
                        Triple("Fajr (الفجر)", todayRecord?.fajrCompleted ?: false) { viewModel.togglePrayer("fajr") },
                        Triple("Dhuhr (الظهر)", todayRecord?.dhuhrCompleted ?: false) { viewModel.togglePrayer("dhuhr") },
                        Triple("Asr (العصر)", todayRecord?.asrCompleted ?: false) { viewModel.togglePrayer("asr") },
                        Triple("Maghrib (المغرب)", todayRecord?.maghribCompleted ?: false) { viewModel.togglePrayer("maghrib") },
                        Triple("Isha (العشاء)", todayRecord?.ishaCompleted ?: false) { viewModel.togglePrayer("isha") }
                    )

                    prayers.forEachIndexed { index, (title, isDone, onToggle) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Checkbox(checked = isDone, onCheckedChange = { onToggle() })
                        }
                        if (index < prayers.size - 1) {
                            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                        }
                    }
                }
            }
        }

        // Daily Azkar & Quran Reading Stepper
        item {
            SectionHeader(title = "Daily Quran & Azkar Habits")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Quran Minutes Stepper
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Quran Reading Time", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("${todayRecord?.quranMinutes ?: 0} minutes today", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val current = todayRecord?.quranMinutes ?: 0
                                viewModel.updateQuranMinutes((current - 5).coerceAtLeast(0))
                            }) {
                                Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease Minutes")
                            }
                            Text("${todayRecord?.quranMinutes ?: 0}m", fontWeight = FontWeight.Bold)
                            IconButton(onClick = {
                                val current = todayRecord?.quranMinutes ?: 0
                                viewModel.updateQuranMinutes(current + 5)
                            }) {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase Minutes")
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Quran Reading Sessions Counter (1 -> 2 -> 3 -> 4...)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Quran Reading Count", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text("${todayRecord?.quranReadingsCount ?: 0} readings recorded today", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                val current = todayRecord?.quranReadingsCount ?: 0
                                viewModel.updateQuranReadingsCount((current - 1).coerceAtLeast(0))
                            }) {
                                Icon(Icons.Filled.RemoveCircleOutline, contentDescription = "Decrease Readings")
                            }
                            Text("${todayRecord?.quranReadingsCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = {
                                val current = todayRecord?.quranReadingsCount ?: 0
                                viewModel.updateQuranReadingsCount(current + 1)
                            }) {
                                Icon(Icons.Filled.AddCircleOutline, contentDescription = "Increase Readings")
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Morning Azkar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.togglePrayer("morning_azkar") }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Morning Azkar (أذكار الصباح)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Checkbox(checked = todayRecord?.morningAzkarCompleted ?: false, onCheckedChange = { viewModel.togglePrayer("morning_azkar") })
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))

                    // Evening Azkar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.togglePrayer("evening_azkar") }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Evening Azkar (أذكار المساء)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Checkbox(checked = todayRecord?.eveningAzkarCompleted ?: false, onCheckedChange = { viewModel.togglePrayer("evening_azkar") })
                    }
                }
            }
        }
    }
}
