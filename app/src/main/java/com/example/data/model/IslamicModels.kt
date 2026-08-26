package com.example.data.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val englishTranslation: String,
    val revelationType: String, // "Meccan" or "Medinan"
    val numberOfAyahs: Int,
    val startPage: Int,
    val juzNumber: Int
)

data class Ayah(
    val numberInSurah: Int,
    val overallNumber: Int,
    val surahNumber: Int,
    val arabicText: String,
    val englishTranslation: String,
    val transliteration: String = "",
    val juz: Int,
    val page: Int,
    val sajda: Boolean = false,
    val audioUrl: String = "" // Standard EveryAyah audio URL architecture
)

data class Hadith(
    val id: String,
    val collection: String, // e.g. "Sahih al-Bukhari", "Sahih Muslim", "40 Hadith Nawawi"
    val bookName: String,
    val hadithNumber: String,
    val chapterTitle: String,
    val narrator: String,
    val arabicText: String,
    val englishTranslation: String,
    val grade: String = "Sahih",
    val category: String // "Faith", "Prayer", "Manners", "Fasting", "Charity", "Dua", "Patience"
)

data class DuaAzkar(
    val id: String,
    val category: String, // "Morning", "Evening", "After Salah", "Sleeping", "Waking Up", "Travel", "Food", "Protection", "Forgiveness", "Daily"
    val title: String,
    val arabicText: String,
    val transliteration: String,
    val englishTranslation: String,
    val reference: String,
    val targetCount: Int = 1,
    val benefit: String = ""
)

data class CityLocation(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneId: String
)

data class JuzInfo(
    val juzNumber: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val startSurahNumber: Int,
    val startAyahNumber: Int,
    val endSurahNumber: Int = startSurahNumber,
    val endAyahNumber: Int = 1,
    val startPage: Int = 1
)
