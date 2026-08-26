package com.example.domain.calculator

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.chrono.HijrahChronology
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

/**
 * Utility to convert Gregorian dates to Hijri dates and format them using the official
 * [java.time.chrono.HijrahDate] and [java.time.chrono.HijrahChronology] APIs (Umm al-Qura calendar).
 */
object HijrahDateConverter {

    val HIJRI_MONTHS_EN = listOf(
        "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
        "Jumada al-Ula", "Jumada al-Akhirah", "Rajab", "Sha'ban",
        "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
    )

    val HIJRI_MONTHS_AR = listOf(
        "المُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الآخِر",
        "جُمَادَى الأُولَى", "جُمَادَى الآخِرَة", "رَجَب", "شَعْبَان",
        "رَمَضَان", "شَوَّال", "ذُو القَعْدَة", "ذُو الحِجَّة"
    )

    val HIJRI_MONTHS_AR_SHORT = listOf(
        "محرم", "صفر", "ربيع الأول", "ربيع الثاني",
        "جمادى الأولى", "جمادى الآخرة", "رجب", "شعبان",
        "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
    )

    private val DAYS_OF_WEEK_EN = mapOf(
        DayOfWeek.MONDAY to "Monday",
        DayOfWeek.TUESDAY to "Tuesday",
        DayOfWeek.WEDNESDAY to "Wednesday",
        DayOfWeek.THURSDAY to "Thursday",
        DayOfWeek.FRIDAY to "Friday",
        DayOfWeek.SATURDAY to "Saturday",
        DayOfWeek.SUNDAY to "Sunday"
    )

    private val DAYS_OF_WEEK_AR = mapOf(
        DayOfWeek.MONDAY to "الإثنين",
        DayOfWeek.TUESDAY to "الثلاثاء",
        DayOfWeek.WEDNESDAY to "الأربعاء",
        DayOfWeek.THURSDAY to "الخميس",
        DayOfWeek.FRIDAY to "الجمعة",
        DayOfWeek.SATURDAY to "السبت",
        DayOfWeek.SUNDAY to "الأحد"
    )

    /**
     * Converts an integer to Eastern Arabic (Hindi-Arabic) numerals: e.g. 14 -> ١٤, 1448 -> ١٤٤٨
     */
    fun toArabicDigits(number: Int): String {
        val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return number.toString().map { char ->
            if (char.isDigit()) arabicDigits[char - '0'] else char
        }.joinToString("")
    }

    /**
     * Converts a [LocalDate] to [HijrahDate] using [HijrahDate.from] and returns a rich [HijriDate] object.
     *
     * @param localDate The Gregorian date (defaults to current date).
     * @param adjustmentDays User-configured day offset for local moon sighting (-2 to +2).
     */
    fun fromLocalDate(
        localDate: LocalDate = LocalDate.now(),
        adjustmentDays: Int = 0
    ): HijriDate {
        val baseHijrahDate = HijrahDate.from(localDate)
        val adjustedHijrahDate = if (adjustmentDays != 0) {
            baseHijrahDate.plus(adjustmentDays.toLong(), ChronoUnit.DAYS) as HijrahDate
        } else {
            baseHijrahDate
        }

        val day = adjustedHijrahDate.get(ChronoField.DAY_OF_MONTH)
        val month = adjustedHijrahDate.get(ChronoField.MONTH_OF_YEAR)
        val year = adjustedHijrahDate.get(ChronoField.YEAR)

        val dayOfWeek = localDate.dayOfWeek
        val isFriday = dayOfWeek == DayOfWeek.FRIDAY
        val isRamadan = month == 9
        val isSacredMonth = month == 1 || month == 7 || month == 11 || month == 12
        val isWhiteDay = day in 13..15

        val monthNameEn = HIJRI_MONTHS_EN.getOrElse(month - 1) { "Month $month" }
        val monthNameAr = HIJRI_MONTHS_AR.getOrElse(month - 1) { "" }

        val dayEn = DAYS_OF_WEEK_EN[dayOfWeek] ?: ""
        val dayAr = DAYS_OF_WEEK_AR[dayOfWeek] ?: ""

        val (moonName, moonIcon) = calculateMoonPhase(day)
        val occasion = detectIslamicOccasion(day, month, isFriday, isWhiteDay, isRamadan)

        return HijriDate(
            day = day,
            month = month,
            year = year,
            monthNameArabic = monthNameAr,
            monthNameEnglish = monthNameEn,
            isRamadan = isRamadan,
            isSacredMonth = isSacredMonth,
            isWhiteDay = isWhiteDay,
            dayOfWeekNameEnglish = dayEn,
            dayOfWeekNameArabic = dayAr,
            dayArabicFormatted = toArabicDigits(day),
            yearArabicFormatted = toArabicDigits(year),
            moonPhaseName = moonName,
            moonPhaseIcon = moonIcon,
            occasionTitle = occasion,
            isFriday = isFriday,
            hijrahDate = adjustedHijrahDate
        )
    }

    /**
     * Converts a Java [Calendar] instance to [HijriDate] using [HijrahDate].
     */
    fun fromCalendar(
        calendar: Calendar = Calendar.getInstance(),
        adjustmentDays: Int = 0
    ): HijriDate {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val localDate = LocalDate.of(year, month, day)
        return fromLocalDate(localDate, adjustmentDays)
    }

    /**
     * Returns the current Hijri date for the given [ZoneId].
     */
    fun now(
        zoneId: ZoneId = ZoneId.systemDefault(),
        adjustmentDays: Int = 0
    ): HijriDate {
        return fromLocalDate(LocalDate.now(zoneId), adjustmentDays)
    }

    /**
     * Formats a [HijrahDate] with a standard Java DateTimeFormatter pattern.
     */
    fun format(
        hijrahDate: HijrahDate,
        pattern: String = "d MMMM yyyy G",
        locale: Locale = Locale.ENGLISH
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            .withChronology(HijrahChronology.INSTANCE)
        return formatter.format(hijrahDate)
    }

    /**
     * Calculates the approximate lunar moon phase for a given Hijri day (1-30).
     */
    fun calculateMoonPhase(hijriDay: Int): Pair<String, String> {
        return when (hijriDay) {
            1 -> "Hilal (New Crescent)" to "🌙"
            in 2..6 -> "Waxing Crescent" to "🌒"
            7, 8 -> "First Quarter" to "🌓"
            in 9..13 -> "Waxing Gibbous" to "🌔"
            14, 15 -> "Full Moon (Badr)" to "🌕"
            16 -> "Waning Full Moon" to "🌕"
            in 17..21 -> "Waning Gibbous" to "🌖"
            22, 23 -> "Third Quarter" to "🌗"
            in 24..28 -> "Waning Crescent" to "🌘"
            else -> "Mahaq (Dark Moon)" to "🌑"
        }
    }

    /**
     * Detects special Islamic days, Sunnah occasions, and major holy dates.
     */
    fun detectIslamicOccasion(
        day: Int,
        month: Int,
        isFriday: Boolean,
        isWhiteDay: Boolean,
        isRamadan: Boolean
    ): String? {
        return when {
            month == 1 && day == 1 -> "Islamic New Year (رأس السنة الهجرية)"
            month == 1 && day == 9 -> "Tasu'a (Fasting Sunnah)"
            month == 1 && day == 10 -> "Day of Ashura (يوم عاشوراء)"
            month == 3 && day == 12 -> "Mawlid an-Nabi ﷺ (المولد النبوي)"
            month == 7 && day == 27 -> "Al-Isra' wal-Mi'raj (الإسراء والمعراج)"
            month == 8 && day == 15 -> "Mid-Sha'ban (ليلة النصف من شعبان)"
            month == 9 && day == 1 -> "First Day of Ramadan (أول رمضان)"
            month == 9 && day == 27 -> "Laylat al-Qadr (ليلة القدر)"
            month == 9 -> "Holy Ramadan Fasting (شهر رمضان المبارك)"
            month == 10 && day == 1 -> "Eid al-Fitr Mubarak (عيد الفطر المبارك)"
            month == 10 && day in 2..3 -> "Eid al-Fitr Holidays"
            month == 12 && day in 1..8 -> "Blessed 10 Days of Dhu al-Hijjah (عشر ذي الحجة)"
            month == 12 && day == 9 -> "Day of Arafah (يوم عرفة - Fasting Sunnah)"
            month == 12 && day == 10 -> "Eid al-Adha Mubarak (عيد الأضحى المبارك)"
            month == 12 && day in 11..13 -> "Days of Tashreeq (أيام التشريق)"
            isFriday -> "Blessed Jummah Mubarak (جمعة مباركة)"
            isWhiteDay -> "Ayyam al-Beed (أيام البيض - White Days Fasting)"
            else -> null
        }
    }
}
