package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAccentDark
import java.text.SimpleDateFormat
import java.util.*

enum class LiveHolyPlace(val title: String, val arabicName: String, val city: String) {
    MAKKAH("Masjid Al-Haram", "المسجد الحرام", "Makkah Al-Mukarramah"),
    MADINAH("Al-Masjid An-Nabawi", "المسجد النبوي", "Madinah Al-Munawwarah")
}

@Composable
fun MakkahMadinahLiveCard(
    modifier: Modifier = Modifier,
    onNavigateToQibla: () -> Unit = {}
) {
    var selectedPlace by remember { mutableStateOf(LiveHolyPlace.MAKKAH) }
    var isLiveActive by remember { mutableStateOf(true) }
    var isAudioPlaying by remember { mutableStateOf(false) }

    val makkahCal = remember { Calendar.getInstance(TimeZone.getTimeZone("Asia/Riyadh")) }
    val makkahTime = remember {
        val sdf = SimpleDateFormat("hh:mm a (z)", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("Asia/Riyadh")
        sdf.format(Date())
    }

    LiquidGlassCard(
        modifier = modifier.testTag("makkah_madinah_live_card"),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row with Tabs and Live Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE53935)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = selectedPlace.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Place Toggle (Makkah / Madinah)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedPlace == LiveHolyPlace.MAKKAH) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.clickable { selectedPlace = LiveHolyPlace.MAKKAH }
                    ) {
                        Text(
                            text = "Makkah",
                            fontSize = 11.sp,
                            fontWeight = if (selectedPlace == LiveHolyPlace.MAKKAH) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedPlace == LiveHolyPlace.MAKKAH) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (selectedPlace == LiveHolyPlace.MADINAH) MaterialTheme.colorScheme.primary else Color.Transparent,
                        modifier = Modifier.clickable { selectedPlace = LiveHolyPlace.MADINAH }
                    ) {
                        Text(
                            text = "Madinah",
                            fontSize = 11.sp,
                            fontWeight = if (selectedPlace == LiveHolyPlace.MADINAH) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedPlace == LiveHolyPlace.MADINAH) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = if (selectedPlace == LiveHolyPlace.MAKKAH) listOf(
                                Color(0xFF0F1E19),
                                Color(0xFF1B382F),
                                Color(0xFF0A1411)
                            ) else listOf(
                                Color(0xFF142436),
                                Color(0xFF1F444E),
                                Color(0xFF0B1B1E)
                            )
                        )
                    )
            ) {
                // Realistic Islamic Architectural Motif Art
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = selectedPlace.arabicName,
                                color = GoldAccentDark,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedPlace.city,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.45f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTime,
                                    contentDescription = null,
                                    tint = GoldAccentDark,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = makkahTime,
                                    color = Color.White,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // Center Visual Icon / Sanctuary Representation
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (selectedPlace == LiveHolyPlace.MAKKAH) Icons.Filled.SquareFoot else Icons.Filled.Mosque,
                                contentDescription = selectedPlace.title,
                                tint = GoldAccentDark.copy(alpha = 0.9f),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (selectedPlace == LiveHolyPlace.MAKKAH) "Holy Kaaba 24/7 Live Stream" else "Prophet's Mosque 24/7 Live Stream",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Bottom Bar inside player
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Hd,
                                contentDescription = "HD Quality",
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1080p Crystal Audio",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 11.sp
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Direct Qibla alignment button
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.clickable { onNavigateToQibla() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Explore,
                                    contentDescription = "Qibla Direction",
                                    tint = Color.White,
                                    modifier = Modifier.padding(6.dp).size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
