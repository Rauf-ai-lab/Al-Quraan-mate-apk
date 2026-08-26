package com.example.domain.calculator

import java.time.chrono.HijrahDate
import java.util.Calendar

data class HijriDate(
    val day: Int,
    val month: Int,
    val year: Int,
    val monthNameArabic: String,
    val monthNameEnglish: String,
    val isRamadan: Boolean,
    val isSacredMonth: Boolean,
    val isWhiteDay: Boolean, // 13, 14, 15 of Hijri month (Sunnah fasting)
    val dayOfWeekNameEnglish: String = "",
    val dayOfWeekNameArabic: String = "",
    val dayArabicFormatted: String = "",
    val yearArabicFormatted: String = "",
    val moonPhaseName: String = "",
    val moonPhaseIcon: String = "🌙",
    val occasionTitle: String? = null,
    val isFriday: Boolean = false,
    val hijrahDate: HijrahDate? = null
) {
    fun formatShort(): String = "$day $monthNameEnglish $year AH"
    fun formatArabic(): String = "${if (dayArabicFormatted.isNotEmpty()) dayArabicFormatted else day.toString()} $monthNameArabic ${if (yearArabicFormatted.isNotEmpty()) yearArabicFormatted else year.toString()} هـ"
    fun formatFull(): String = "$day $monthNameEnglish $year AH • ${if (dayArabicFormatted.isNotEmpty()) dayArabicFormatted else day.toString()} $monthNameArabic $year هـ"
}

data class IslamicEvent(
    val nameEnglish: String,
    val nameArabic: String,
    val hijriDay: Int,
    val hijriMonth: Int,
    val description: String
)

object HijriCalendarCalculator {

    val HIJRI_MONTHS_EN = HijrahDateConverter.HIJRI_MONTHS_EN
    val HIJRI_MONTHS_AR = HijrahDateConverter.HIJRI_MONTHS_AR

    val ISLAMIC_EVENTS = listOf(
        IslamicEvent("Islamic New Year", "رأس السنة الهجرية", 1, 1, "First day of Muharram marking the Hijra."),
        IslamicEvent("Day of Ashura", "يوم عاشوراء", 10, 1, "Day Prophet Musa (AS) was saved from Pharaoh."),
        IslamicEvent("Mawlid an-Nabi", "المولد النبوي", 12, 3, "Commemoration of the birth of the Prophet Muhammad ﷺ."),
        IslamicEvent("Isra and Mi'raj", "الإسراء والمعراج", 27, 7, "The Miraculous Night Journey and Ascension."),
        IslamicEvent("Mid-Sha'ban (Laylat al-Bara'at)", "ليلة النصف من شعبان", 15, 8, "Night of records and seeking forgiveness."),
        IslamicEvent("First Day of Ramadan", "أول أيام رمضان", 1, 9, "Beginning of the blessed holy month of fasting."),
        IslamicEvent("Laylat al-Qadr (Night of Power)", "ليلة القدر", 27, 9, "The Night of Decree, better than a thousand months."),
        IslamicEvent("Eid al-Fitr", "عيد الفطر المبارك", 1, 10, "Celebration of the completion of Ramadan."),
        IslamicEvent("Day of Arafah", "يوم عرفة", 9, 12, "The greatest day of Hajj, highly recommended to fast."),
        IslamicEvent("Eid al-Adha", "عيد الأضحى المبارك", 10, 12, "Feast of the Sacrifice commemorating Prophet Ibrahim (AS)."),
        IslamicEvent("Days of Tashreeq", "أيام التشريق", 11, 12, "Days of eating, drinking and remembrance of Allah.")
    )

    /**
     * Converts Gregorian calendar date to Hijri date using the official java.time.chrono.HijrahDate API,
     * applying user offset adjustment (-2, -1, 0, +1, +2 days).
     */
    fun calculateHijriDate(calendar: Calendar = Calendar.getInstance(), adjustmentDays: Int = 0): HijriDate {
        return HijrahDateConverter.fromCalendar(calendar, adjustmentDays)
    }

    fun getUpcomingEvents(currentHijriDate: HijriDate): List<Pair<IslamicEvent, Int>> {
        return ISLAMIC_EVENTS.map { event ->
            var daysAway = (event.hijriMonth - currentHijriDate.month) * 30 + (event.hijriDay - currentHijriDate.day)
            if (daysAway < 0) {
                daysAway += 354 // Next Hijri year
            }
            event to daysAway
        }.sortedBy { it.second }
    }
}
