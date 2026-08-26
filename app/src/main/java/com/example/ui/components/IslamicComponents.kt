package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.domain.calculator.HijriDate
import com.example.ui.theme.*

/**
 * Prominent Hero Hijri Date Card for displaying Gregorian to Hijri date conversions
 * powered by the official java.time.chrono.HijrahDate API.
 */
@Composable
fun ProminentHijriHeroCard(
    modifier: Modifier = Modifier,
    hijriDate: HijriDate?,
    gregorianDateText: String,
    cityName: String,
    hijriAdjustment: Int = 0,
    onViewCalendar: () -> Unit,
    onAdjustOffset: ((Int) -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("prominent_hijri_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            IslamicGreenLight,
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Bismillah + HijrahDate / Umm al-Qura Engine Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = GoldAccentDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        border = BorderStroke(0.5.dp, GoldAccentDark.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = hijriDate?.moonPhaseIcon ?: "🌙",
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "HijrahDate API",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldAccentDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Core Prominent Hijri Date Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: Large Day Number Badge (Dual Arabic & Western)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.22f),
                        border = BorderStroke(1.5.dp, GoldAccentDark.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .size(76.dp)
                            .testTag("hijri_date_number")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = hijriDate?.dayArabicFormatted?.ifEmpty { hijriDate.day.toString() } ?: "--",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccentDark,
                                lineHeight = 28.sp
                            )
                            Text(
                                text = "Day ${hijriDate?.day ?: "--"}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Center / Right: Prominent Hijri Month, Year & Arabic Calligraphy
                    Column(modifier = Modifier.weight(1f)) {
                        // Arabic Month & Year
                        Text(
                            text = if (hijriDate != null) "${hijriDate.dayArabicFormatted} ${hijriDate.monthNameArabic} ${hijriDate.yearArabicFormatted} هـ" else "التقويم الهجري",
                            color = GoldAccentDark,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("hijri_month_arabic")
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // English Month & Year
                        Text(
                            text = if (hijriDate != null) "${hijriDate.day} ${hijriDate.monthNameEnglish} ${hijriDate.year} AH" else "Hijri Calendar",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.2.sp,
                            modifier = Modifier.testTag("hijri_month_english")
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Day of week (English & Arabic)
                        val dayOfWeekStr = if (hijriDate != null && hijriDate.dayOfWeekNameEnglish.isNotEmpty()) {
                            "${hijriDate.dayOfWeekNameEnglish} • ${hijriDate.dayOfWeekNameArabic}"
                        } else {
                            "Islamic Lunar Date"
                        }
                        Text(
                            text = dayOfWeekStr,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Dynamic Occasion Banner (if applicable)
                if (hijriDate?.occasionTitle != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldAccentDark.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, GoldAccentDark.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth().testTag("hijri_occasion_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = GoldAccentDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = hijriDate.occasionTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                // Bottom Footer Row: Gregorian Date Conversion, Moon Phase & Quick Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CalendarToday,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = gregorianDateText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.LocationOn,
                                contentDescription = null,
                                tint = GoldAccentDark,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "$cityName • ${hijriDate?.moonPhaseName ?: "Moon Phase"}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Quick Calendar & Offset Pills
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onViewCalendar() }
                                .testTag("hijri_calendar_button"),
                            shape = RoundedCornerShape(10.dp),
                            color = GoldAccentDark,
                            contentColor = IslamicSurfaceDark
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CalendarMonth,
                                    contentDescription = "Hijri Calendar",
                                    tint = IslamicSurfaceDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Calendar",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (onAdjustOffset != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        val next = if (hijriAdjustment >= 2) -2 else hijriAdjustment + 1
                                        onAdjustOffset(next)
                                    }
                                    .testTag("hijri_offset_chip"),
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.15f),
                                border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = if (hijriAdjustment >= 0) "+${hijriAdjustment}d" else "${hijriAdjustment}d",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IslamicBannerCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    arabicGreeting: String = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
    icon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = arabicGreeting,
                    color = GoldAccentDark,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 13.sp
                        )
                    }

                    if (trailingContent != null) {
                        trailingContent()
                    } else if (icon != null) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = GoldAccentDark,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val glassFill = if (isDark) {
        Color(0xFF13221B).copy(alpha = 0.85f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 0.92f)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        color = glassFill,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.12f),
                    borderColor.copy(alpha = 0.45f)
                )
            )
        ),
        shadowElevation = if (isDark) 4.dp else 2.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun RealisticIslamicNavVisual(
    route: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val glowBg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent

    Box(
        modifier = modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(glowBg),
        contentAlignment = Alignment.Center
    ) {
        when (route) {
            "home" -> Icon(
                imageVector = if (isSelected) Icons.Filled.Mosque else Icons.Outlined.Mosque,
                contentDescription = "Home Sanctuary",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "offline_quran" -> Icon(
                imageVector = if (isSelected) Icons.Filled.AutoStories else Icons.Outlined.AutoStories,
                contentDescription = "Offline Quran Mushaf",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "quran" -> Icon(
                imageVector = if (isSelected) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                contentDescription = "Audio Quran",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "prayer" -> Icon(
                imageVector = if (isSelected) Icons.Filled.AccessTimeFilled else Icons.Outlined.AccessTime,
                contentDescription = "Prayer Times Minaret",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "discover" -> Icon(
                imageVector = if (isSelected) Icons.Filled.Explore else Icons.Outlined.Explore,
                contentDescription = "Astrolabe Discover",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "tracker" -> Icon(
                imageVector = if (isSelected) Icons.Filled.TaskAlt else Icons.Outlined.CheckCircleOutline,
                contentDescription = "Deen Tracker Tasbeeh",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            "settings" -> Icon(
                imageVector = if (isSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                contentDescription = "Settings Arabesque",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            else -> Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
