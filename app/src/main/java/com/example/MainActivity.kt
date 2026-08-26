package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicDataSource
import com.example.data.model.Surah
import com.example.ui.MainViewModel
import com.example.ui.components.RealisticIslamicNavVisual
import com.example.ui.navigation.DiscoverTab
import com.example.ui.navigation.Screen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.prayer.PrayerScreen
import com.example.ui.screens.quran.QuranScreen
import com.example.ui.screens.quran.SurahReaderScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.screens.tracker.TrackerScreen
import com.example.ui.theme.DeenMateTheme
import com.example.ui.theme.GoldAccentDark

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by viewModel.userSettings.collectAsState()
            val systemDark = isSystemInDarkTheme()
            val isDarkTheme = when (userSettings.appTheme) {
                "LIGHT" -> false
                "DARK" -> true
                else -> systemDark
            }

            DeenMateTheme(darkTheme = isDarkTheme) {
                DeenMateMainApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeenMateMainApp(viewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf<String>(Screen.Home.route) }
    var activeDiscoverTab by remember { mutableStateOf(DiscoverTab.QIBLA) }
    var activeSurahForReading by remember { mutableStateOf<Surah?>(null) }
    var activeJuzForReading by remember { mutableStateOf<Int?>(null) }

    val homeState by viewModel.homeUiState.collectAsState()

    val navItems = listOf(
        Screen.Home,
        Screen.Quran,
        Screen.Prayer,
        Screen.Discover,
        Screen.Tracker,
        Screen.Settings
    )

    // Android System Back Gesture Handler
    // If reading a surah, back takes user back to Quran list; if on any other tab, back returns to Home; if on Home, exits app.
    BackHandler(enabled = activeSurahForReading != null || activeJuzForReading != null || currentScreen != Screen.Home.route) {
        when {
            activeSurahForReading != null || activeJuzForReading != null -> {
                activeSurahForReading = null
                activeJuzForReading = null
            }
            currentScreen != Screen.Home.route -> {
                currentScreen = Screen.Home.route
            }
        }
    }

    // If reading a specific Surah or Juz, show the dedicated SurahReaderScreen
    if (activeSurahForReading != null || activeJuzForReading != null) {
        SurahReaderScreen(
            initialSurah = activeSurahForReading,
            initialJuzNumber = activeJuzForReading,
            viewModel = viewModel,
            onBack = {
                activeSurahForReading = null
                activeJuzForReading = null
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Mosque,
                                contentDescription = "DeenMate Logo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "DeenMate",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "by Rauf",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                            homeState.hijriDate?.let { hijri ->
                                Text(
                                    text = "${hijri.formatShort()} • ${homeState.userSettings.cityName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            currentScreen = Screen.Search.route
                        },
                        modifier = Modifier.testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                navItems.forEach { screen ->
                    val isSelected = currentScreen == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen.route },
                        icon = {
                            RealisticIslamicNavVisual(
                                route = screen.route,
                                isSelected = isSelected
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_navigation"
            ) { screenRoute ->
                when (screenRoute) {
                    Screen.Home.route -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToScreen = { currentScreen = it },
                            onNavigateToDiscoverTab = { tab ->
                                activeDiscoverTab = tab
                                currentScreen = Screen.Discover.route
                            },
                            onOpenSurah = { surahNum ->
                                val surah = IslamicDataSource.SURAHS.find { it.number == surahNum } ?: IslamicDataSource.SURAHS[0]
                                activeSurahForReading = surah
                            }
                        )
                    }
                    Screen.Quran.route -> {
                        QuranScreen(
                            viewModel = viewModel,
                            onOpenSurahReader = { surah, juzNum ->
                                activeSurahForReading = surah
                                activeJuzForReading = juzNum
                            }
                        )
                    }
                    Screen.Prayer.route -> {
                        PrayerScreen(viewModel = viewModel)
                    }
                    Screen.Discover.route -> {
                        DiscoverScreen(
                            viewModel = viewModel,
                            initialTab = activeDiscoverTab
                        )
                    }
                    Screen.Search.route -> {
                        SearchScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Home.route },
                            onOpenSurah = { surahNum ->
                                val surah = IslamicDataSource.SURAHS.find { it.number == surahNum } ?: IslamicDataSource.SURAHS[0]
                                activeSurahForReading = surah
                            },
                            onNavigateToDiscoverTab = { tab ->
                                activeDiscoverTab = tab
                                currentScreen = Screen.Discover.route
                            }
                        )
                    }
                    Screen.Tracker.route -> {
                        TrackerScreen(viewModel = viewModel)
                    }
                    Screen.Settings.route -> {
                        SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
