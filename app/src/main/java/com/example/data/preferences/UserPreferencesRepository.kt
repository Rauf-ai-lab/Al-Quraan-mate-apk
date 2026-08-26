package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.domain.calculator.CalculationMethod
import com.example.domain.calculator.HighLatitudeRule
import com.example.domain.calculator.Madhhab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "deenmate_settings")

data class UserSettings(
    val calculationMethod: CalculationMethod = CalculationMethod.MUSLIM_WORLD_LEAGUE,
    val madhhab: Madhhab = Madhhab.STANDARD,
    val highLatitudeRule: HighLatitudeRule = HighLatitudeRule.ANGLE_BASED,
    val cityName: String = "Makkah",
    val countryName: String = "Saudi Arabia",
    val latitude: Double = 21.4225,
    val longitude: Double = 39.8262,
    val timeZoneId: String = "Asia/Riyadh",
    val useAutoGps: Boolean = false,
    val hijriAdjustment: Int = 0,
    val notificationsEnabled: Boolean = true,
    val fajrNotification: Boolean = true,
    val dhuhrNotification: Boolean = true,
    val asrNotification: Boolean = true,
    val maghribNotification: Boolean = true,
    val ishaNotification: Boolean = true,
    val morningAzkarNotification: Boolean = true,
    val eveningAzkarNotification: Boolean = true,
    val dailyAyahNotification: Boolean = true,
    val dailyHadithNotification: Boolean = true,
    val arabicFontSize: Float = 26f,
    val showTranslation: Boolean = true,
    val showTransliteration: Boolean = true,
    val appTheme: String = "SYSTEM", // "SYSTEM", "LIGHT", "DARK"
    val vibrationOnTasbeeh: Boolean = true,
    val selectedReciterId: String = "mishary",
    val useCustomPrayerTimes: Boolean = false,
    val customFajrTime: String = "05:00",
    val customDhuhrTime: String = "12:30",
    val customAsrTime: String = "15:45",
    val customMaghribTime: String = "18:20",
    val customIshaTime: String = "19:45",
    val customFajrAdhanTime: String = "05:00",
    val customDhuhrAdhanTime: String = "12:30",
    val customAsrAdhanTime: String = "15:45",
    val customMaghribAdhanTime: String = "18:20",
    val customIshaAdhanTime: String = "19:45",
    val adhanSoundName: String = "Makkah Adhan",
    val is24HourFormat: Boolean = false,
    val quranFontFamily: String = "Uthmani", // "Uthmani", "Amiri", "Scheherazade", "IndoPak", "Naskh"
    val hasCustomProfile: Boolean = false,
    val profileName: String = "",
    val profileBio: String = "",
    val profileAvatar: String = "crescent"
)

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val CALC_METHOD = stringPreferencesKey("calc_method")
        val MADHHAB = stringPreferencesKey("madhhab")
        val HIGH_LAT_RULE = stringPreferencesKey("high_lat_rule")
        val CITY_NAME = stringPreferencesKey("city_name")
        val COUNTRY_NAME = stringPreferencesKey("country_name")
        val LATITUDE = doublePreferencesKey("latitude")
        val LONGITUDE = doublePreferencesKey("longitude")
        val TIME_ZONE_ID = stringPreferencesKey("time_zone_id")
        val USE_AUTO_GPS = booleanPreferencesKey("use_auto_gps")
        val HIJRI_ADJUSTMENT = intPreferencesKey("hijri_adjustment")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val FAJR_NOTIF = booleanPreferencesKey("fajr_notif")
        val DHUHR_NOTIF = booleanPreferencesKey("dhuhr_notif")
        val ASR_NOTIF = booleanPreferencesKey("asr_notif")
        val MAGHRIB_NOTIF = booleanPreferencesKey("maghrib_notif")
        val ISHA_NOTIF = booleanPreferencesKey("isha_notif")
        val MORNING_AZKAR_NOTIF = booleanPreferencesKey("morning_azkar_notif")
        val EVENING_AZKAR_NOTIF = booleanPreferencesKey("evening_azkar_notif")
        val DAILY_AYAH_NOTIF = booleanPreferencesKey("daily_ayah_notif")
        val DAILY_HADITH_NOTIF = booleanPreferencesKey("daily_hadith_notif")
        val ARABIC_FONT_SIZE = floatPreferencesKey("arabic_font_size")
        val SHOW_TRANSLATION = booleanPreferencesKey("show_translation")
        val SHOW_TRANSLITERATION = booleanPreferencesKey("show_transliteration")
        val APP_THEME = stringPreferencesKey("app_theme")
        val VIBRATION_TASBEEH = booleanPreferencesKey("vibration_tasbeeh")
        val RECITER_ID = stringPreferencesKey("reciter_id")
        val USE_CUSTOM_PRAYER_TIMES = booleanPreferencesKey("use_custom_prayer_times")
        val CUSTOM_FAJR = stringPreferencesKey("custom_fajr")
        val CUSTOM_DHUHR = stringPreferencesKey("custom_dhuhr")
        val CUSTOM_ASR = stringPreferencesKey("custom_asr")
        val CUSTOM_MAGHRIB = stringPreferencesKey("custom_maghrib")
        val CUSTOM_ISHA = stringPreferencesKey("custom_isha")
        val CUSTOM_FAJR_ADHAN = stringPreferencesKey("custom_fajr_adhan")
        val CUSTOM_DHUHR_ADHAN = stringPreferencesKey("custom_dhuhr_adhan")
        val CUSTOM_ASR_ADHAN = stringPreferencesKey("custom_asr_adhan")
        val CUSTOM_MAGHRIB_ADHAN = stringPreferencesKey("custom_maghrib_adhan")
        val CUSTOM_ISHA_ADHAN = stringPreferencesKey("custom_isha_adhan")
        val ADHAN_SOUND = stringPreferencesKey("adhan_sound")
        val IS_24_HOUR_FORMAT = booleanPreferencesKey("is_24_hour_format")
        val QURAN_FONT_FAMILY = stringPreferencesKey("quran_font_family")
        val HAS_CUSTOM_PROFILE = booleanPreferencesKey("has_custom_profile")
        val PROFILE_NAME = stringPreferencesKey("profile_name")
        val PROFILE_BIO = stringPreferencesKey("profile_bio")
        val PROFILE_AVATAR = stringPreferencesKey("profile_avatar")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            calculationMethod = CalculationMethod.valueOf(
                prefs[PreferencesKeys.CALC_METHOD] ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
            ),
            madhhab = Madhhab.valueOf(
                prefs[PreferencesKeys.MADHHAB] ?: Madhhab.STANDARD.name
            ),
            highLatitudeRule = HighLatitudeRule.valueOf(
                prefs[PreferencesKeys.HIGH_LAT_RULE] ?: HighLatitudeRule.ANGLE_BASED.name
            ),
            cityName = prefs[PreferencesKeys.CITY_NAME] ?: "Makkah",
            countryName = prefs[PreferencesKeys.COUNTRY_NAME] ?: "Saudi Arabia",
            latitude = prefs[PreferencesKeys.LATITUDE] ?: 21.4225,
            longitude = prefs[PreferencesKeys.LONGITUDE] ?: 39.8262,
            timeZoneId = prefs[PreferencesKeys.TIME_ZONE_ID] ?: "Asia/Riyadh",
            useAutoGps = prefs[PreferencesKeys.USE_AUTO_GPS] ?: false,
            hijriAdjustment = prefs[PreferencesKeys.HIJRI_ADJUSTMENT] ?: 0,
            notificationsEnabled = prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true,
            fajrNotification = prefs[PreferencesKeys.FAJR_NOTIF] ?: true,
            dhuhrNotification = prefs[PreferencesKeys.DHUHR_NOTIF] ?: true,
            asrNotification = prefs[PreferencesKeys.ASR_NOTIF] ?: true,
            maghribNotification = prefs[PreferencesKeys.MAGHRIB_NOTIF] ?: true,
            ishaNotification = prefs[PreferencesKeys.ISHA_NOTIF] ?: true,
            morningAzkarNotification = prefs[PreferencesKeys.MORNING_AZKAR_NOTIF] ?: true,
            eveningAzkarNotification = prefs[PreferencesKeys.EVENING_AZKAR_NOTIF] ?: true,
            dailyAyahNotification = prefs[PreferencesKeys.DAILY_AYAH_NOTIF] ?: true,
            dailyHadithNotification = prefs[PreferencesKeys.DAILY_HADITH_NOTIF] ?: true,
            arabicFontSize = prefs[PreferencesKeys.ARABIC_FONT_SIZE] ?: 26f,
            showTranslation = prefs[PreferencesKeys.SHOW_TRANSLATION] ?: true,
            showTransliteration = prefs[PreferencesKeys.SHOW_TRANSLITERATION] ?: true,
            appTheme = prefs[PreferencesKeys.APP_THEME] ?: "SYSTEM",
            vibrationOnTasbeeh = prefs[PreferencesKeys.VIBRATION_TASBEEH] ?: true,
            selectedReciterId = prefs[PreferencesKeys.RECITER_ID] ?: "mishary",
            useCustomPrayerTimes = prefs[PreferencesKeys.USE_CUSTOM_PRAYER_TIMES] ?: false,
            customFajrTime = prefs[PreferencesKeys.CUSTOM_FAJR] ?: "05:00",
            customDhuhrTime = prefs[PreferencesKeys.CUSTOM_DHUHR] ?: "12:30",
            customAsrTime = prefs[PreferencesKeys.CUSTOM_ASR] ?: "15:45",
            customMaghribTime = prefs[PreferencesKeys.CUSTOM_MAGHRIB] ?: "18:20",
            customIshaTime = prefs[PreferencesKeys.CUSTOM_ISHA] ?: "19:45",
            customFajrAdhanTime = prefs[PreferencesKeys.CUSTOM_FAJR_ADHAN] ?: "05:00",
            customDhuhrAdhanTime = prefs[PreferencesKeys.CUSTOM_DHUHR_ADHAN] ?: "12:30",
            customAsrAdhanTime = prefs[PreferencesKeys.CUSTOM_ASR_ADHAN] ?: "15:45",
            customMaghribAdhanTime = prefs[PreferencesKeys.CUSTOM_MAGHRIB_ADHAN] ?: "18:20",
            customIshaAdhanTime = prefs[PreferencesKeys.CUSTOM_ISHA_ADHAN] ?: "19:45",
            adhanSoundName = prefs[PreferencesKeys.ADHAN_SOUND] ?: "Makkah Adhan",
            is24HourFormat = prefs[PreferencesKeys.IS_24_HOUR_FORMAT] ?: false,
            quranFontFamily = prefs[PreferencesKeys.QURAN_FONT_FAMILY] ?: "Uthmani",
            hasCustomProfile = prefs[PreferencesKeys.HAS_CUSTOM_PROFILE] ?: false,
            profileName = prefs[PreferencesKeys.PROFILE_NAME] ?: "",
            profileBio = prefs[PreferencesKeys.PROFILE_BIO] ?: "",
            profileAvatar = prefs[PreferencesKeys.PROFILE_AVATAR] ?: "crescent"
        )
    }

    suspend fun updateCalculationMethod(method: CalculationMethod) {
        context.dataStore.edit { it[PreferencesKeys.CALC_METHOD] = method.name }
    }

    suspend fun updateMadhhab(madhhab: Madhhab) {
        context.dataStore.edit { it[PreferencesKeys.MADHHAB] = madhhab.name }
    }

    suspend fun updateLocation(city: String, country: String, lat: Double, lng: Double, tz: String, isGps: Boolean) {
        context.dataStore.edit {
            it[PreferencesKeys.CITY_NAME] = city
            it[PreferencesKeys.COUNTRY_NAME] = country
            it[PreferencesKeys.LATITUDE] = lat
            it[PreferencesKeys.LONGITUDE] = lng
            it[PreferencesKeys.TIME_ZONE_ID] = tz
            it[PreferencesKeys.USE_AUTO_GPS] = isGps
        }
    }

    suspend fun updateHijriAdjustment(adjustment: Int) {
        context.dataStore.edit { it[PreferencesKeys.HIJRI_ADJUSTMENT] = adjustment }
    }

    suspend fun updatePrayerNotification(prayerName: String, enabled: Boolean) {
        context.dataStore.edit {
            when (prayerName.lowercase()) {
                "fajr" -> it[PreferencesKeys.FAJR_NOTIF] = enabled
                "dhuhr" -> it[PreferencesKeys.DHUHR_NOTIF] = enabled
                "asr" -> it[PreferencesKeys.ASR_NOTIF] = enabled
                "maghrib" -> it[PreferencesKeys.MAGHRIB_NOTIF] = enabled
                "isha" -> it[PreferencesKeys.ISHA_NOTIF] = enabled
                "all" -> it[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
            }
        }
    }

    suspend fun updateDailyReminders(type: String, enabled: Boolean) {
        context.dataStore.edit {
            when (type.lowercase()) {
                "morning" -> it[PreferencesKeys.MORNING_AZKAR_NOTIF] = enabled
                "evening" -> it[PreferencesKeys.EVENING_AZKAR_NOTIF] = enabled
                "ayah" -> it[PreferencesKeys.DAILY_AYAH_NOTIF] = enabled
                "hadith" -> it[PreferencesKeys.DAILY_HADITH_NOTIF] = enabled
            }
        }
    }

    suspend fun updateArabicFontSize(size: Float) {
        context.dataStore.edit { it[PreferencesKeys.ARABIC_FONT_SIZE] = size }
    }

    suspend fun updateShowTranslation(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_TRANSLATION] = show }
    }

    suspend fun updateShowTransliteration(show: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.SHOW_TRANSLITERATION] = show }
    }

    suspend fun updateAppTheme(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.APP_THEME] = theme }
    }

    suspend fun updateTasbeehVibration(vibrate: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.VIBRATION_TASBEEH] = vibrate }
    }

    suspend fun updateSelectedReciter(reciterId: String) {
        context.dataStore.edit { it[PreferencesKeys.RECITER_ID] = reciterId }
    }

    suspend fun updateUseCustomPrayerTimes(enabled: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.USE_CUSTOM_PRAYER_TIMES] = enabled }
    }

    suspend fun updateCustomPrayerTime(prayerKey: String, timeString: String) {
        context.dataStore.edit {
            when (prayerKey.lowercase()) {
                "fajr" -> it[PreferencesKeys.CUSTOM_FAJR] = timeString
                "dhuhr" -> it[PreferencesKeys.CUSTOM_DHUHR] = timeString
                "asr" -> it[PreferencesKeys.CUSTOM_ASR] = timeString
                "maghrib" -> it[PreferencesKeys.CUSTOM_MAGHRIB] = timeString
                "isha" -> it[PreferencesKeys.CUSTOM_ISHA] = timeString
            }
        }
    }

    suspend fun updateCustomAdhanTime(prayerKey: String, timeString: String) {
        context.dataStore.edit {
            when (prayerKey.lowercase()) {
                "fajr" -> it[PreferencesKeys.CUSTOM_FAJR_ADHAN] = timeString
                "dhuhr" -> it[PreferencesKeys.CUSTOM_DHUHR_ADHAN] = timeString
                "asr" -> it[PreferencesKeys.CUSTOM_ASR_ADHAN] = timeString
                "maghrib" -> it[PreferencesKeys.CUSTOM_MAGHRIB_ADHAN] = timeString
                "isha" -> it[PreferencesKeys.CUSTOM_ISHA_ADHAN] = timeString
            }
        }
    }

    suspend fun updateAdhanSound(soundName: String) {
        context.dataStore.edit { it[PreferencesKeys.ADHAN_SOUND] = soundName }
    }

    suspend fun update24HourFormat(is24Hour: Boolean) {
        context.dataStore.edit { it[PreferencesKeys.IS_24_HOUR_FORMAT] = is24Hour }
    }

    suspend fun resetCustomPrayerTimes(defaultFajr: String, defaultDhuhr: String, defaultAsr: String, defaultMaghrib: String, defaultIsha: String) {
        context.dataStore.edit {
            it[PreferencesKeys.USE_CUSTOM_PRAYER_TIMES] = false
            it[PreferencesKeys.CUSTOM_FAJR] = defaultFajr
            it[PreferencesKeys.CUSTOM_DHUHR] = defaultDhuhr
            it[PreferencesKeys.CUSTOM_ASR] = defaultAsr
            it[PreferencesKeys.CUSTOM_MAGHRIB] = defaultMaghrib
            it[PreferencesKeys.CUSTOM_ISHA] = defaultIsha
            it[PreferencesKeys.CUSTOM_FAJR_ADHAN] = defaultFajr
            it[PreferencesKeys.CUSTOM_DHUHR_ADHAN] = defaultDhuhr
            it[PreferencesKeys.CUSTOM_ASR_ADHAN] = defaultAsr
            it[PreferencesKeys.CUSTOM_MAGHRIB_ADHAN] = defaultMaghrib
            it[PreferencesKeys.CUSTOM_ISHA_ADHAN] = defaultIsha
        }
    }

    suspend fun updateQuranFontFamily(fontFamily: String) {
        context.dataStore.edit { it[PreferencesKeys.QURAN_FONT_FAMILY] = fontFamily }
    }

    suspend fun saveUserProfile(name: String, bio: String, avatar: String) {
        context.dataStore.edit {
            it[PreferencesKeys.HAS_CUSTOM_PROFILE] = true
            it[PreferencesKeys.PROFILE_NAME] = name
            it[PreferencesKeys.PROFILE_BIO] = bio
            it[PreferencesKeys.PROFILE_AVATAR] = avatar
        }
    }

    suspend fun deleteUserProfile() {
        context.dataStore.edit {
            it[PreferencesKeys.HAS_CUSTOM_PROFILE] = false
            it[PreferencesKeys.PROFILE_NAME] = ""
            it[PreferencesKeys.PROFILE_BIO] = ""
            it[PreferencesKeys.PROFILE_AVATAR] = "crescent"
        }
    }
}
