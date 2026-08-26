package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.QuranAudioPlayerManager
import com.example.audio.QuranPlaybackState
import com.example.audio.ReciterVoice
import com.example.audio.ReciterVoicePacks
import com.example.data.local.AppDatabase
import com.example.data.local.BookmarkEntity
import com.example.data.local.DeenDayEntity
import com.example.data.local.IslamicDataSource
import com.example.data.model.*
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.DeenRepository
import com.example.domain.calculator.*
import com.example.location.CompassSensorManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HomeUiState(
    val gregorianDateText: String = "",
    val hijriDate: HijriDate? = null,
    val prayerTimes: PrayerTimesResult? = null,
    val deenScore: Int = 0,
    val todayDeenRecord: DeenDayEntity? = null,
    val dailyAyah: Ayah? = null,
    val dailyAyahSurahName: String = "",
    val dailyHadith: Hadith? = null,
    val dailyDua: DuaAzkar? = null,
    val userSettings: UserSettings = UserSettings()
)

data class QiblaUiState(
    val qiblaBearing: Float = 0f,
    val distanceKm: Double = 0.0,
    val deviceAzimuth: Float = 0f,
    val isAlignedWithKaaba: Boolean = false,
    val accuracy: Int = 3
)

data class TasbeehUiState(
    val currentCount: Int = 0,
    val targetCount: Int = 33,
    val selectedDhikr: String = "SubhanAllah",
    val todayTotal: Int = 0,
    val streakDays: Int = 3
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val deenRepository = DeenRepository(db)
    val preferencesRepository = UserPreferencesRepository(application)
    val quranRepository = com.example.data.repository.QuranRepository(application)
    private val compassSensorManager = CompassSensorManager(application)
    val quranAudioPlayerManager = QuranAudioPlayerManager(application)

    val userSettings = preferencesRepository.userSettingsFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        UserSettings()
    )

    val quranPlaybackState: StateFlow<QuranPlaybackState> = quranAudioPlayerManager.playbackState

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState.asStateFlow()

    private val _qiblaUiState = MutableStateFlow(QiblaUiState())
    val qiblaUiState: StateFlow<QiblaUiState> = _qiblaUiState.asStateFlow()

    private val _tasbeehUiState = MutableStateFlow(TasbeehUiState())
    val tasbeehUiState: StateFlow<TasbeehUiState> = _tasbeehUiState.asStateFlow()

    // Quran State
    val surahsList = IslamicDataSource.SURAHS
    private val _selectedSurah = MutableStateFlow<Surah?>(IslamicDataSource.SURAHS[0])
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    private val _selectedSurahAyahs = MutableStateFlow<List<Ayah>>(IslamicDataSource.AYAHS_BY_SURAH[1] ?: emptyList())
    val selectedSurahAyahs: StateFlow<List<Ayah>> = _selectedSurahAyahs.asStateFlow()

    private val _quranSearchQuery = MutableStateFlow("")
    val quranSearchQuery: StateFlow<String> = _quranSearchQuery.asStateFlow()

    // Hadith State
    val hadithsList = IslamicDataSource.HADITHS
    private val _selectedHadithCategory = MutableStateFlow("All")
    val selectedHadithCategory: StateFlow<String> = _selectedHadithCategory.asStateFlow()

    private val _hadithSearchQuery = MutableStateFlow("")
    val hadithSearchQuery: StateFlow<String> = _hadithSearchQuery.asStateFlow()

    // Dua & Azkar State
    val duasList = IslamicDataSource.DUAS_AND_AZKAR
    private val _selectedDuaCategory = MutableStateFlow("Morning")
    val selectedDuaCategory: StateFlow<String> = _selectedDuaCategory.asStateFlow()

    // Bookmarks and Last Read
    val allBookmarks = deenRepository.getAllBookmarks().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val lastRead = deenRepository.getLastRead().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val todayDeenRecord = deenRepository.getTodayDeenRecord().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        null
    )

    val recentDeenRecords = deenRepository.getRecentDeenRecords(30).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    val completedFastsCount = deenRepository.getCompletedFastsCount().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    // Global Search State
    private val _globalSearchQuery = MutableStateFlow("")
    val globalSearchQuery: StateFlow<String> = _globalSearchQuery.asStateFlow()

    private val _globalSearchFilter = MutableStateFlow("ALL") // "ALL", "QURAN", "HADITH", "DUA"
    val globalSearchFilter: StateFlow<String> = _globalSearchFilter.asStateFlow()

    init {
        // Daily items
        val dailyAyahItem = IslamicDataSource.AYAHS_BY_SURAH[2]?.find { it.numberInSurah == 255 } ?: IslamicDataSource.AYAHS_BY_SURAH[1]?.first()
        val dailyHadithItem = IslamicDataSource.HADITHS.first()
        val dailyDuaItem = IslamicDataSource.DUAS_AND_AZKAR.first()

        viewModelScope.launch {
            // Live countdown timer loop
            while (true) {
                updateDashboard(dailyAyahItem, dailyHadithItem, dailyDuaItem)
                delay(1000)
            }
        }

        // Observe compass azimuth for Qibla
        viewModelScope.launch {
            compassSensorManager.azimuthFlow.collect { azimuth ->
                val settings = userSettings.value
                val qiblaResult = QiblaCalculator.calculate(settings.latitude, settings.longitude)
                val diff = kotlin.math.abs(azimuth - qiblaResult.bearing)
                val isAligned = diff <= 3f || diff >= 357f

                _qiblaUiState.value = QiblaUiState(
                    qiblaBearing = qiblaResult.bearing,
                    distanceKm = qiblaResult.distanceKm,
                    deviceAzimuth = azimuth,
                    isAlignedWithKaaba = isAligned,
                    accuracy = compassSensorManager.accuracyFlow.value
                )
            }
        }

        // Observe Tasbeeh total for today
        viewModelScope.launch {
            deenRepository.getTodayTasbeehTotal().collect { total ->
                _tasbeehUiState.update { it.copy(todayTotal = total ?: 0) }
            }
        }
    }

    private fun updateDashboard(dailyAyahItem: Ayah?, dailyHadithItem: Hadith, dailyDuaItem: DuaAzkar) {
        val settings = userSettings.value
        val now = Calendar.getInstance(TimeZone.getTimeZone(settings.timeZoneId))

        val gregFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
        val gregText = gregFormat.format(now.time)

        val hijri = HijriCalendarCalculator.calculateHijriDate(now, settings.hijriAdjustment)

        val rawPrayerRes = PrayerTimesCalculator.calculate(
            latitude = settings.latitude,
            longitude = settings.longitude,
            date = now,
            method = settings.calculationMethod,
            madhhab = settings.madhhab,
            highLatRule = settings.highLatitudeRule,
            timeZone = TimeZone.getTimeZone(settings.timeZoneId)
        )

        val prayerRes = PrayerTimesCalculator.applyCustomTimes(
            calculated = rawPrayerRes,
            useCustom = settings.useCustomPrayerTimes,
            customFajr = settings.customFajrTime,
            customDhuhr = settings.customDhuhrTime,
            customAsr = settings.customAsrTime,
            customMaghrib = settings.customMaghribTime,
            customIsha = settings.customIshaTime,
            timeZone = TimeZone.getTimeZone(settings.timeZoneId)
        )

        val todayRec = todayDeenRecord.value

        _homeUiState.value = HomeUiState(
            gregorianDateText = gregText,
            hijriDate = hijri,
            prayerTimes = prayerRes,
            deenScore = todayRec?.calculateScore() ?: 0,
            todayDeenRecord = todayRec,
            dailyAyah = dailyAyahItem,
            dailyAyahSurahName = "Al-Baqarah (2:255)",
            dailyHadith = dailyHadithItem,
            dailyDua = dailyDuaItem,
            userSettings = settings
        )
    }

    fun startCompass() = compassSensorManager.startListening()
    fun stopCompass() = compassSensorManager.stopListening()

    // Quran actions
    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
        // Provide immediate local fallback ayahs
        val localAyahs = IslamicDataSource.getAyahsForSurah(surah)
        _selectedSurahAyahs.value = localAyahs
        quranAudioPlayerManager.setSurahAyahs(localAyahs)

        viewModelScope.launch {
            deenRepository.setLastRead(surah.number, 1, surah.nameArabic, surah.nameEnglish)
            try {
                val fullAyahs = quranRepository.getAyahsForSurah(surah)
                if (fullAyahs.isNotEmpty() && _selectedSurah.value?.number == surah.number) {
                    _selectedSurahAyahs.value = fullAyahs
                    quranAudioPlayerManager.setSurahAyahs(fullAyahs)
                }
            } catch (e: Exception) {
                android.util.Log.w("MainViewModel", "Failed fetching full Surah text", e)
            }
        }
    }

    fun selectJuz(juzNumber: Int) {
        stopQuranAudio()
        val juzInfo = IslamicDataSource.JUZ_DEFINITIONS.find { it.juzNumber == juzNumber }
            ?: IslamicDataSource.JUZ_DEFINITIONS[0]
        val matchingSurah = IslamicDataSource.SURAHS.firstOrNull { it.number == juzInfo.startSurahNumber }
            ?: IslamicDataSource.SURAHS[0]
        _selectedSurah.value = matchingSurah
        val localAyahs = IslamicDataSource.getAyahsForJuz(juzNumber)
        _selectedSurahAyahs.value = localAyahs
        quranAudioPlayerManager.setSurahAyahs(localAyahs)

        viewModelScope.launch {
            deenRepository.setLastRead(matchingSurah.number, juzInfo.startAyahNumber, matchingSurah.nameArabic, matchingSurah.nameEnglish)
            try {
                val fullAyahs = quranRepository.getAyahsForJuz(juzNumber)
                if (fullAyahs.isNotEmpty()) {
                    _selectedSurahAyahs.value = fullAyahs
                    quranAudioPlayerManager.setSurahAyahs(fullAyahs)
                }
            } catch (e: Exception) {
                android.util.Log.w("MainViewModel", "Failed fetching full Juz text", e)
            }
        }
    }

    fun playQuranAyah(surah: Surah, ayah: Ayah) {
        quranAudioPlayerManager.playAyah(surah, ayah)
    }

    fun resumeLastAudioSession() {
        val settings = userSettings.value
        if (settings.hasSavedAudioSession) {
            val surah = IslamicDataSource.SURAHS.find { it.number == settings.lastAudioSurahNumber } ?: IslamicDataSource.SURAHS[0]
            _selectedSurah.value = surah
            val ayahs = IslamicDataSource.getAyahsForSurah(surah)
            _selectedSurahAyahs.value = ayahs
            quranAudioPlayerManager.resumeRecitation(
                surahNumber = settings.lastAudioSurahNumber,
                ayahNumber = settings.lastAudioAyahNumber,
                positionMs = settings.lastAudioPositionMs,
                reciterId = settings.lastAudioReciterId
            )
        }
    }

    fun saveOfflineProgress(surahNumber: Int, ayahNumber: Int, page: Int = 1) {
        viewModelScope.launch {
            preferencesRepository.saveOfflineQuranProgress(surahNumber, ayahNumber, page)
            val surah = IslamicDataSource.SURAHS.find { it.number == surahNumber }
            if (surah != null) {
                deenRepository.setLastRead(surahNumber, ayahNumber, surah.nameArabic, surah.nameEnglish)
            }
        }
    }

    fun toggleQuranPlayPause() {
        quranAudioPlayerManager.togglePlayPause()
    }

    fun stopQuranAudio() {
        quranAudioPlayerManager.stop()
    }

    fun nextQuranAyah() {
        quranAudioPlayerManager.nextAyah()
    }

    fun previousQuranAyah() {
        quranAudioPlayerManager.previousAyah()
    }

    fun selectReciterVoice(reciter: ReciterVoice) {
        quranAudioPlayerManager.selectReciter(reciter)
        viewModelScope.launch {
            preferencesRepository.updateSelectedReciter(reciter.id)
        }
    }

    fun setQuranPlaybackSpeed(speed: Float) {
        quranAudioPlayerManager.setPlaybackSpeed(speed)
    }

    fun seekQuranAudio(progress: Float) {
        quranAudioPlayerManager.seekTo(progress)
    }

    private fun generateFallbackAyahsForSurah(surah: Surah): List<Ayah> {
        return (1..surah.numberOfAyahs.coerceAtMost(20)).map { num ->
            Ayah(
                numberInSurah = num,
                overallNumber = num,
                surahNumber = surah.number,
                arabicText = if (num == 1 && surah.number != 9) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ" else "آية كريمة مباركة من سورة ${surah.nameArabic} ($num)",
                englishTranslation = "Blessed verse $num from Surah ${surah.nameEnglish} (${surah.englishTranslation}).",
                transliteration = "Ayah $num min Surah ${surah.nameEnglish}",
                juz = surah.juzNumber,
                page = surah.startPage
            )
        }
    }

    fun updateQuranSearchQuery(query: String) {
        _quranSearchQuery.value = query
    }

    // Hadith actions
    fun selectHadithCategory(category: String) {
        _selectedHadithCategory.value = category
    }

    fun updateHadithSearchQuery(query: String) {
        _hadithSearchQuery.value = query
    }

    // Dua actions
    fun selectDuaCategory(category: String) {
        _selectedDuaCategory.value = category
    }

    // Tasbeeh actions
    fun incrementTasbeeh() {
        val current = _tasbeehUiState.value.currentCount + 1
        val target = _tasbeehUiState.value.targetCount

        if (userSettings.value.vibrationOnTasbeeh) {
            triggerHaptic(if (target > 0 && current % target == 0) 80L else 30L)
        }

        _tasbeehUiState.update { it.copy(currentCount = current) }

        viewModelScope.launch {
            deenRepository.addTasbeehCount(_tasbeehUiState.value.selectedDhikr, 1, target)
        }
    }

    fun resetTasbeeh() {
        _tasbeehUiState.update { it.copy(currentCount = 0) }
    }

    fun setTasbeehTarget(target: Int) {
        _tasbeehUiState.update { it.copy(targetCount = target) }
    }

    fun setTasbeehDhikr(dhikr: String) {
        _tasbeehUiState.update { it.copy(selectedDhikr = dhikr, currentCount = 0) }
    }

    // Deen Tracker Actions
    fun togglePrayer(prayerName: String) {
        viewModelScope.launch {
            deenRepository.togglePrayer(prayerName)
        }
    }

    fun updateQuranMinutes(minutes: Int) {
        viewModelScope.launch {
            deenRepository.updateQuranMinutes(minutes)
        }
    }

    fun incrementQuranReading() {
        viewModelScope.launch {
            deenRepository.incrementQuranReading()
        }
    }

    fun updateQuranReadingsCount(count: Int) {
        viewModelScope.launch {
            deenRepository.updateQuranReadingsCount(count)
        }
    }

    fun updateFastingStatus(status: String) {
        viewModelScope.launch {
            deenRepository.updateFastingStatus(status)
        }
    }

    // Bookmarking
    fun toggleBookmark(type: String, id: String, title: String, subtitle: String, arSnippet: String, enSnippet: String, destData: String) {
        viewModelScope.launch {
            val existing = allBookmarks.value.find { it.id == id }
            if (existing != null) {
                deenRepository.removeBookmark(id)
            } else {
                deenRepository.toggleBookmark(
                    BookmarkEntity(
                        id = id,
                        type = type,
                        title = title,
                        subtitle = subtitle,
                        arabicSnippet = arSnippet,
                        englishSnippet = enSnippet,
                        destinationData = destData
                    )
                )
            }
        }
    }

    // Location selection
    fun selectCity(city: CityLocation) {
        viewModelScope.launch {
            preferencesRepository.updateLocation(
                city = city.name,
                country = city.country,
                lat = city.latitude,
                lng = city.longitude,
                tz = city.timeZoneId,
                isGps = false
            )
        }
    }

    fun setGpsLocation(lat: Double, lng: Double, cityName: String = "My Location") {
        viewModelScope.launch {
            preferencesRepository.updateLocation(
                city = cityName,
                country = "",
                lat = lat,
                lng = lng,
                tz = TimeZone.getDefault().id,
                isGps = true
            )
        }
    }

    // Global Search
    fun updateGlobalSearchQuery(query: String) {
        _globalSearchQuery.value = query
    }

    fun setGlobalSearchFilter(filter: String) {
        _globalSearchFilter.value = filter
    }

    // Custom Prayer & Adhan Times
    fun setUseCustomPrayerTimes(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateUseCustomPrayerTimes(enabled)
        }
    }

    fun updateCustomPrayerTime(prayerKey: String, time: String) {
        viewModelScope.launch {
            preferencesRepository.updateCustomPrayerTime(prayerKey, time)
        }
    }

    fun updateCustomAdhanTime(prayerKey: String, time: String) {
        viewModelScope.launch {
            preferencesRepository.updateCustomAdhanTime(prayerKey, time)
        }
    }

    fun updateAdhanSound(soundName: String) {
        viewModelScope.launch {
            preferencesRepository.updateAdhanSound(soundName)
        }
    }

    fun updatePrayerNotification(prayerName: String, enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updatePrayerNotification(prayerName, enabled)
        }
    }

    fun update24HourFormat(is24Hour: Boolean) {
        viewModelScope.launch {
            preferencesRepository.update24HourFormat(is24Hour)
        }
    }

    fun toggle24HourFormat() {
        viewModelScope.launch {
            val current = userSettings.value.is24HourFormat
            preferencesRepository.update24HourFormat(!current)
        }
    }

    fun resetCustomPrayerTimes(defaultFajr: String, defaultDhuhr: String, defaultAsr: String, defaultMaghrib: String, defaultIsha: String) {
        viewModelScope.launch {
            preferencesRepository.resetCustomPrayerTimes(defaultFajr, defaultDhuhr, defaultAsr, defaultMaghrib, defaultIsha)
        }
    }

    fun updateHijriAdjustment(adjustment: Int) {
        viewModelScope.launch {
            preferencesRepository.updateHijriAdjustment(adjustment.coerceIn(-2, 2))
        }
    }

    fun triggerHaptic(durationMs: Long = 40L) {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(durationMs)
                }
            }
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        quranAudioPlayerManager.release()
    }
}
