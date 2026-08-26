package com.example

import com.example.audio.ReciterVoicePacks
import com.example.data.local.IslamicDataSource
import com.example.domain.calculator.HijrahDateConverter
import com.example.domain.calculator.HijriCalendarCalculator
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.util.Calendar

class ExampleUnitTest {

    @Test
    fun reciterVoicePacks_containsAllRequiredReciters() {
        val reciters = ReciterVoicePacks.RECITERS
        assertTrue(reciters.isNotEmpty())
        assertTrue(reciters.any { it.id == "mishary" })
        assertTrue(reciters.any { it.id == "sudais" })
        assertTrue(reciters.any { it.id == "muaiqly" })
        assertTrue(reciters.any { it.id == "ghamdi" })
        assertTrue(reciters.any { it.id == "husary" })
        assertEquals("mishary", ReciterVoicePacks.DEFAULT_RECITER.id)
    }

    @Test
    fun hijrahDateConverter_convertsGregorianToHijriSuccessfully() {
        // Test a known date: 2026-08-26
        val testDate = LocalDate.of(2026, 8, 26)
        val hijriDate = HijrahDateConverter.fromLocalDate(testDate)

        assertNotNull(hijriDate)
        assertTrue(hijriDate.year >= 1447)
        assertTrue(hijriDate.month in 1..12)
        assertTrue(hijriDate.day in 1..30)
        assertTrue(hijriDate.monthNameEnglish.isNotEmpty())
        assertTrue(hijriDate.monthNameArabic.isNotEmpty())
        assertNotNull(hijriDate.hijrahDate)

        // Check Arabic numeral conversion
        val arabicDay = HijrahDateConverter.toArabicDigits(14)
        assertEquals("١٤", arabicDay)

        val arabicYear = HijrahDateConverter.toArabicDigits(1448)
        assertEquals("١٤٤٨", arabicYear)
    }

    @Test
    fun hijrahDateConverter_supportsAdjustmentDays() {
        val testDate = LocalDate.of(2026, 8, 26)
        val baseDate = HijrahDateConverter.fromLocalDate(testDate, adjustmentDays = 0)
        val plusOneDay = HijrahDateConverter.fromLocalDate(testDate, adjustmentDays = 1)
        val minusOneDay = HijrahDateConverter.fromLocalDate(testDate, adjustmentDays = -1)

        assertNotNull(baseDate)
        assertNotNull(plusOneDay)
        assertNotNull(minusOneDay)
    }

    @Test
    fun hijriCalendarCalculator_integratesWithHijrahDateConverter() {
        val cal = Calendar.getInstance()
        cal.set(2026, Calendar.AUGUST, 26)
        val result = HijriCalendarCalculator.calculateHijriDate(cal)

        assertNotNull(result)
        assertTrue(result.formatShort().contains("AH"))
        assertTrue(result.formatArabic().contains("هـ"))
    }

    @Test
    fun islamicDataSource_containsSurahsAndAyahs() {
        val surahs = IslamicDataSource.SURAHS
        assertEquals(114, surahs.size)
        val fatihaAyahs = IslamicDataSource.AYAHS_BY_SURAH[1]
        assertNotNull(fatihaAyahs)
        assertEquals(7, fatihaAyahs?.size)
    }
}
