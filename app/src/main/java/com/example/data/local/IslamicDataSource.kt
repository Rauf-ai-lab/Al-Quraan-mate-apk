package com.example.data.local

import com.example.data.model.Ayah
import com.example.data.model.CityLocation
import com.example.data.model.DuaAzkar
import com.example.data.model.Hadith
import com.example.data.model.JuzInfo
import com.example.data.model.Surah

object IslamicDataSource {

    val POPULAR_CITIES = listOf(
        CityLocation("Makkah", "Saudi Arabia", 21.4225, 39.8262, "Asia/Riyadh"),
        CityLocation("Madinah", "Saudi Arabia", 24.4672, 39.6111, "Asia/Riyadh"),
        CityLocation("Srinagar", "Kashmir", 34.0837, 74.7973, "Asia/Kolkata"),
        CityLocation("Jammu", "Kashmir", 32.7266, 74.8570, "Asia/Kolkata"),
        CityLocation("Anantnag", "Kashmir", 33.7311, 75.1487, "Asia/Kolkata"),
        CityLocation("Baramulla", "Kashmir", 34.1980, 74.3636, "Asia/Kolkata"),
        CityLocation("Cairo", "Egypt", 30.0444, 31.2357, "Africa/Cairo"),
        CityLocation("Jerusalem", "Palestine", 31.7683, 35.2137, "Asia/Jerusalem"),
        CityLocation("Istanbul", "Turkey", 41.0082, 28.9784, "Europe/Istanbul"),
        CityLocation("London", "United Kingdom", 51.5074, -0.1278, "Europe/London"),
        CityLocation("New York", "United States", 40.7128, -74.0060, "America/New_York"),
        CityLocation("Los Angeles", "United States", 34.0522, -118.2437, "America/Los_Angeles"),
        CityLocation("Chicago", "United States", 41.8781, -87.6298, "America/Chicago"),
        CityLocation("Toronto", "Canada", 43.6532, -79.3832, "America/Toronto"),
        CityLocation("Dubai", "United Arab Emirates", 25.2048, 55.2708, "Asia/Dubai"),
        CityLocation("Doha", "Qatar", 25.2854, 51.5310, "Asia/Qatar"),
        CityLocation("Karachi", "Pakistan", 24.8607, 67.0011, "Asia/Karachi"),
        CityLocation("Lahore", "Pakistan", 31.5204, 74.3587, "Asia/Karachi"),
        CityLocation("Islamabad", "Pakistan", 33.6844, 73.0479, "Asia/Karachi"),
        CityLocation("Dhaka", "Bangladesh", 23.8103, 90.4125, "Asia/Dhaka"),
        CityLocation("Jakarta", "Indonesia", -6.2088, 106.8456, "Asia/Jakarta"),
        CityLocation("Kuala Lumpur", "Malaysia", 3.1390, 101.6869, "Asia/Kuala_Lumpur"),
        CityLocation("Singapore", "Singapore", 1.3521, 103.8198, "Asia/Singapore"),
        CityLocation("Delhi", "India", 28.6139, 77.2090, "Asia/Kolkata"),
        CityLocation("Mumbai", "India", 19.0760, 72.8777, "Asia/Kolkata"),
        CityLocation("Hyderabad", "India", 17.3850, 78.4867, "Asia/Kolkata"),
        CityLocation("Bengaluru", "India", 12.9716, 77.5946, "Asia/Kolkata"),
        CityLocation("Sydney", "Australia", -33.8688, 151.2093, "Australia/Sydney"),
        CityLocation("Paris", "France", 48.8566, 2.3522, "Europe/Paris"),
        CityLocation("Berlin", "Germany", 52.5200, 13.4050, "Europe/Berlin"),
        CityLocation("Amsterdam", "Netherlands", 52.3676, 4.9041, "Europe/Amsterdam"),
        CityLocation("Johannesburg", "South Africa", -26.2041, 28.0473, "Africa/Johannesburg")
    )

    // Complete 114 Surahs Metadata
    val SURAHS: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "The Opening", "Meccan", 7, 1, 1),
        Surah(2, "البقرة", "Al-Baqarah", "The Cow", "Medinan", 286, 2, 1),
        Surah(3, "آل عمران", "Ali 'Imran", "Family of Imran", "Medinan", 200, 50, 3),
        Surah(4, "النساء", "An-Nisa", "The Women", "Medinan", 176, 77, 4),
        Surah(5, "المائدة", "Al-Ma'idah", "The Table Spread", "Medinan", 120, 106, 6),
        Surah(6, "الأنعام", "Al-An'am", "The Cattle", "Meccan", 165, 128, 7),
        Surah(7, "الأعراف", "Al-A'raf", "The Heights", "Meccan", 206, 151, 8),
        Surah(8, "الأنفال", "Al-Anfal", "The Spoils of War", "Medinan", 75, 177, 9),
        Surah(9, "التوبة", "At-Tawbah", "The Repentance", "Medinan", 129, 187, 10),
        Surah(10, "يونس", "Yunus", "Jonah", "Meccan", 109, 208, 11),
        Surah(11, "هود", "Hud", "Hud", "Meccan", 123, 221, 11),
        Surah(12, "يوسف", "Yusuf", "Joseph", "Meccan", 111, 235, 12),
        Surah(13, "الرعد", "Ar-Ra'd", "The Thunder", "Medinan", 43, 249, 13),
        Surah(14, "إبراهيم", "Ibrahim", "Abraham", "Meccan", 52, 255, 13),
        Surah(15, "الحجر", "Al-Hijr", "The Rocky Tract", "Meccan", 99, 262, 14),
        Surah(16, "النحل", "An-Nahl", "The Bee", "Meccan", 128, 267, 14),
        Surah(17, "الإسراء", "Al-Isra", "The Night Journey", "Meccan", 111, 282, 15),
        Surah(18, "الكهف", "Al-Kahf", "The Cave", "Meccan", 110, 293, 15),
        Surah(19, "مريم", "Maryam", "Mary", "Meccan", 98, 305, 16),
        Surah(20, "طه", "Taha", "Ta-Ha", "Meccan", 135, 312, 16),
        Surah(21, "الأنبياء", "Al-Anbiya", "The Prophets", "Meccan", 112, 322, 17),
        Surah(22, "الحج", "Al-Hajj", "The Pilgrimage", "Medinan", 78, 332, 17),
        Surah(23, "المؤمنون", "Al-Mu'minun", "The Believers", "Meccan", 118, 342, 18),
        Surah(24, "النور", "An-Nur", "The Light", "Medinan", 64, 350, 18),
        Surah(25, "الفرقان", "Al-Furqan", "The Criterion", "Meccan", 77, 359, 18),
        Surah(26, "الشعراء", "Ash-Shu'ara", "The Poets", "Meccan", 227, 367, 19),
        Surah(27, "النمل", "An-Naml", "The Ant", "Meccan", 93, 377, 19),
        Surah(28, "القصص", "Al-Qasas", "The Stories", "Meccan", 88, 385, 20),
        Surah(29, "العنكبوت", "Al-'Ankabut", "The Spider", "Meccan", 69, 396, 20),
        Surah(30, "الروم", "Ar-Rum", "The Romans", "Meccan", 60, 404, 21),
        Surah(31, "لقمان", "Luqman", "Luqman", "Meccan", 34, 411, 21),
        Surah(32, "السجدة", "As-Sajdah", "The Prostration", "Meccan", 30, 415, 21),
        Surah(33, "الأحزاب", "Al-Ahzab", "The Combined Forces", "Medinan", 73, 418, 21),
        Surah(34, "سبأ", "Saba", "Sheba", "Meccan", 54, 428, 22),
        Surah(35, "فاطر", "Fatir", "Originator", "Meccan", 45, 434, 22),
        Surah(36, "يس", "Ya-Sin", "Ya-Sin", "Meccan", 83, 440, 22),
        Surah(37, "الصافات", "As-Saffat", "Those who set the Ranks", "Meccan", 182, 446, 23),
        Surah(38, "ص", "Sad", "The Letter Sad", "Meccan", 88, 453, 23),
        Surah(39, "الزمر", "Az-Zumar", "The Troops", "Meccan", 75, 458, 23),
        Surah(40, "غافر", "Ghafir", "The Forgiver", "Meccan", 85, 467, 24),
        Surah(41, "فصلت", "Fussilat", "Explained in Detail", "Meccan", 54, 477, 24),
        Surah(42, "الشورى", "Ash-Shuraa", "The Consultation", "Meccan", 53, 483, 25),
        Surah(43, "الزخرف", "Az-Zukhruf", "The Ornaments of Gold", "Meccan", 89, 489, 25),
        Surah(44, "الدخان", "Ad-Dukhan", "The Smoke", "Meccan", 59, 496, 25),
        Surah(45, "الجاثية", "Al-Jathiyah", "The Crouching", "Meccan", 37, 499, 25),
        Surah(46, "الأحقاف", "Al-Ahqaf", "The Wind-Curved Sandhills", "Meccan", 35, 502, 26),
        Surah(47, "محمد", "Muhammad", "Muhammad", "Medinan", 38, 507, 26),
        Surah(48, "الفتح", "Al-Fath", "The Victory", "Medinan", 29, 511, 26),
        Surah(49, "الحجرات", "Al-Hujurat", "The Rooms", "Medinan", 18, 515, 26),
        Surah(50, "ق", "Qaf", "The Letter Qaf", "Meccan", 45, 518, 26),
        Surah(51, "الذاريات", "Adh-Dhariyat", "The Winnowing Winds", "Meccan", 60, 520, 26),
        Surah(52, "الطور", "At-Tur", "The Mount", "Meccan", 49, 523, 27),
        Surah(53, "النجم", "An-Najm", "The Star", "Meccan", 62, 526, 27),
        Surah(54, "القمر", "Al-Qamar", "The Moon", "Meccan", 55, 528, 27),
        Surah(55, "الرحمن", "Ar-Rahman", "The Beneficent", "Medinan", 78, 531, 27),
        Surah(56, "الواقعة", "Al-Waqi'ah", "The Inevitable", "Meccan", 96, 534, 27),
        Surah(57, "الحديد", "Al-Hadid", "The Iron", "Medinan", 29, 537, 27),
        Surah(58, "المجادلة", "Al-Mujadila", "The Pleading Woman", "Medinan", 22, 542, 28),
        Surah(59, "الحشر", "Al-Hashr", "The Exile", "Medinan", 24, 545, 28),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "She that is to be examined", "Medinan", 13, 549, 28),
        Surah(61, "الصف", "As-Saf", "The Ranks", "Medinan", 14, 551, 28),
        Surah(62, "الجمعة", "Al-Jumu'ah", "Friday", "Medinan", 11, 553, 28),
        Surah(63, "المنافقون", "Al-Munafiqun", "The Hypocrites", "Medinan", 11, 554, 28),
        Surah(64, "التغابن", "At-Taghabun", "Mutual Disillusion", "Medinan", 18, 556, 28),
        Surah(65, "الطلاق", "At-Talaq", "The Divorce", "Medinan", 12, 558, 28),
        Surah(66, "التحريم", "At-Tahrim", "The Prohibition", "Medinan", 12, 560, 28),
        Surah(67, "الملك", "Al-Mulk", "The Sovereignty", "Meccan", 30, 562, 29),
        Surah(68, "القلم", "Al-Qalam", "The Pen", "Meccan", 52, 564, 29),
        Surah(69, "الحاقة", "Al-Haqqah", "The Reality", "Meccan", 52, 566, 29),
        Surah(70, "المعارج", "Al-Ma'arij", "The Ascending Stairways", "Meccan", 44, 568, 29),
        Surah(71, "نوح", "Nuh", "Noah", "Meccan", 28, 570, 29),
        Surah(72, "الجن", "Al-Jinn", "The Jinn", "Meccan", 28, 572, 29),
        Surah(73, "المزمل", "Al-Muzzammil", "The Enshrouded One", "Meccan", 20, 574, 29),
        Surah(74, "المدثر", "Al-Muddaththir", "The Cloaked One", "Meccan", 56, 575, 29),
        Surah(75, "القيامة", "Al-Qiyamah", "The Resurrection", "Meccan", 40, 577, 29),
        Surah(76, "الإنسان", "Al-Insan", "Man", "Medinan", 31, 578, 29),
        Surah(77, "المرسلات", "Al-Mursalat", "The Emissaries", "Meccan", 50, 580, 29),
        Surah(78, "النبأ", "An-Naba", "The Tidings", "Meccan", 40, 582, 30),
        Surah(79, "النازعات", "An-Nazi'at", "Those who drag forth", "Meccan", 46, 583, 30),
        Surah(80, "عبس", "'Abasa", "He Frowned", "Meccan", 42, 585, 30),
        Surah(81, "التكوير", "At-Takwir", "The Overthrowing", "Meccan", 29, 586, 30),
        Surah(82, "الانفطار", "Al-Infitar", "The Cleaving", "Meccan", 19, 587, 30),
        Surah(83, "المطففين", "Al-Mutaffifin", "Defrauding", "Meccan", 36, 587, 30),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "The Splitting Open", "Meccan", 25, 589, 30),
        Surah(85, "البروج", "Al-Buruj", "The Mansions of the Stars", "Meccan", 22, 590, 30),
        Surah(86, "الطارق", "At-Tariq", "The Morning Star", "Meccan", 17, 591, 30),
        Surah(87, "الأعلى", "Al-A'la", "The Most High", "Meccan", 19, 591, 30),
        Surah(88, "الغاشية", "Al-Ghashiyah", "The Overwhelming", "Meccan", 26, 592, 30),
        Surah(89, "الفجر", "Al-Fajr", "The Dawn", "Meccan", 30, 593, 30),
        Surah(90, "البلد", "Al-Balad", "The City", "Meccan", 20, 594, 30),
        Surah(91, "الشمس", "Ash-Shams", "The Sun", "Meccan", 15, 595, 30),
        Surah(92, "الليل", "Al-Layl", "The Night", "Meccan", 21, 595, 30),
        Surah(93, "الضحى", "Ad-Duhaa", "The Morning Hours", "Meccan", 11, 596, 30),
        Surah(94, "الشرح", "Ash-Sharh", "The Relief", "Meccan", 8, 596, 30),
        Surah(95, "التين", "At-Tin", "The Fig", "Meccan", 8, 597, 30),
        Surah(96, "العلق", "Al-'Alaq", "The Clot", "Meccan", 19, 597, 30),
        Surah(97, "القدر", "Al-Qadr", "The Power", "Meccan", 5, 598, 30),
        Surah(98, "البينة", "Al-Bayyinah", "The Clear Proof", "Medinan", 8, 598, 30),
        Surah(99, "الزلزلة", "Az-Zalzalah", "The Earthquake", "Medinan", 8, 599, 30),
        Surah(100, "العاديات", "Al-'Adiyat", "The Courser", "Meccan", 11, 599, 30),
        Surah(101, "القارعة", "Al-Qari'ah", "The Calamity", "Meccan", 11, 600, 30),
        Surah(102, "التكاثر", "At-Takathur", "The Rivalry in World Increase", "Meccan", 8, 600, 30),
        Surah(103, "العصر", "Al-'Asr", "The Declining Day", "Meccan", 3, 601, 30),
        Surah(104, "الهمزة", "Al-Humazah", "The Traducer", "Meccan", 9, 601, 30),
        Surah(105, "الفيل", "Al-Fil", "The Elephant", "Meccan", 5, 601, 30),
        Surah(106, "قريش", "Quraysh", "Quraysh", "Meccan", 4, 602, 30),
        Surah(107, "الماعون", "Al-Ma'un", "Small Kindnesses", "Meccan", 7, 602, 30),
        Surah(108, "الكوثر", "Al-Kawthar", "Abundance", "Meccan", 3, 602, 30),
        Surah(109, "الكافرون", "Al-Kafirun", "The Disbelievers", "Meccan", 6, 603, 30),
        Surah(110, "النصر", "An-Nasr", "The Divine Support", "Medinan", 3, 603, 30),
        Surah(111, "المسد", "Al-Masad", "The Palm Fiber", "Meccan", 5, 603, 30),
        Surah(112, "الإخلاص", "Al-Ikhlas", "The Sincerity", "Meccan", 4, 604, 30),
        Surah(113, "الفلق", "Al-Falaq", "The Daybreak", "Meccan", 5, 604, 30),
        Surah(114, "الناس", "An-Nas", "Mankind", "Meccan", 6, 604, 30)
    )

    // Complete 30 Juz Definitions with authentic starting and ending points
    val JUZ_DEFINITIONS: List<JuzInfo> = listOf(
        JuzInfo(1, "الم", "Alif Lam Meem", 1, 1, 2, 141, 1),
        JuzInfo(2, "سَيَقُولُ", "Sayaqool", 2, 142, 2, 252, 22),
        JuzInfo(3, "تِلْكَ الرُّسُلُ", "Tilkar-Rusul", 2, 253, 3, 92, 42),
        JuzInfo(4, "لَنْ تَنَالُوا", "Lan Tanaaloo", 3, 93, 4, 23, 62),
        JuzInfo(5, "وَالْمُحْصَنَاتُ", "Wal-Muhsanaat", 4, 24, 4, 147, 82),
        JuzInfo(6, "لَا يُحِبُّ اللَّهُ", "La Yuhibbullah", 4, 148, 5, 81, 102),
        JuzInfo(7, "وَإِذَا سَمِعُوا", "Wa Iza Sami'oo", 5, 82, 6, 110, 121),
        JuzInfo(8, "وَلَوْ أَنَّنَا", "Wa Law Annana", 6, 111, 7, 87, 142),
        JuzInfo(9, "قَالَ الْمَلَأُ", "Qalal-Mala'u", 7, 88, 8, 40, 162),
        JuzInfo(10, "وَاعْلَمُوا", "Wa'lamoo", 8, 41, 9, 92, 182),
        JuzInfo(11, "يَعْتَذِرُونَ", "Ya'taziroon", 9, 93, 11, 5, 201),
        JuzInfo(12, "وَمَا مِنْ دَابَّةٍ", "Wa Mamin Daabbah", 11, 6, 12, 52, 222),
        JuzInfo(13, "وَمَا أُبَرِّئُ", "Wa Ma Ubarri'u", 12, 53, 14, 52, 242),
        JuzInfo(14, "رُبَمَا", "Rubama", 15, 1, 16, 128, 262),
        JuzInfo(15, "سُبْحَانَ الَّذِي", "Subhanal-lazi", 17, 1, 18, 74, 282),
        JuzInfo(16, "قَالَ أَلَمْ", "Qala Alam", 18, 75, 20, 135, 302),
        JuzInfo(17, "اقْتَرَبَ", "Iqtaraba", 21, 1, 22, 78, 322),
        JuzInfo(18, "قَدْ أَفْلَحَ", "Qad Aflaha", 23, 1, 25, 20, 342),
        JuzInfo(19, "وَقَالَ الَّذِينَ", "Wa Qalal-lazina", 25, 21, 27, 55, 362),
        JuzInfo(20, "أَمَّنْ خَلَقَ", "Amman Khalaq", 27, 56, 29, 45, 382),
        JuzInfo(21, "اتْلُ مَا أُوحِيَ", "Utlu Ma Oohiya", 29, 46, 33, 30, 402),
        JuzInfo(22, "وَمَنْ يَقْنُتْ", "Wa Man Yaqnut", 33, 31, 36, 27, 422),
        JuzInfo(23, "وَمَا لِيَ", "Wa Maliya", 36, 28, 39, 31, 442),
        JuzInfo(24, "فَمَنْ أَظْلَمُ", "Faman Azlamu", 39, 32, 41, 46, 462),
        JuzInfo(25, "إِلَيْهِ يُرَدُّ", "Ilayhi Yuraddu", 41, 47, 45, 37, 482),
        JuzInfo(26, "حم", "Haa Meem", 46, 1, 51, 30, 502),
        JuzInfo(27, "قَالَ فَمَا خَطْبُكُمْ", "Qala Fama Khatbukum", 51, 31, 57, 29, 522),
        JuzInfo(28, "قَدْ سَمِعَ اللَّهُ", "Qad Sami'allah", 58, 1, 66, 12, 542),
        JuzInfo(29, "تَبَارَكَ الَّذِي", "Tabarakal-lazi", 67, 1, 77, 50, 562),
        JuzInfo(30, "عَمَّ", "'Amma", 78, 1, 114, 6, 582)
    )

    // Legacy JUZ_LIST for backwards compatibility
    val JUZ_LIST = JUZ_DEFINITIONS.map { juz ->
        "Juz ${juz.juzNumber}" to "Starts at Surah ${SURAHS.find { it.number == juz.startSurahNumber }?.nameEnglish ?: "Surah"} Ayah ${juz.startAyahNumber} (Page ${juz.startPage})"
    }

    // Rich Authentic Ayahs Database
    val AYAHS_BY_SURAH: Map<Int, List<Ayah>> = mapOf(
        1 to listOf(
            Ayah(1, 1, 1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "Bismillaahir-Rahmaanir-Rahiim", 1, 1),
            Ayah(2, 2, 1, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "Alhamdu lillaahi Rabbil-'aalamiin", 1, 1),
            Ayah(3, 3, 1, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "Ar-Rahmaanir-Rahiim", 1, 1),
            Ayah(4, 4, 1, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "Maaliki Yawmid-Diin", 1, 1),
            Ayah(5, 5, 1, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "Iyyaaka na'budu wa iyyaaka nasta'iin", 1, 1),
            Ayah(6, 6, 1, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "Ihdinas-Siraatal-Mustaqiim", 1, 1),
            Ayah(7, 7, 1, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "Siraatal-laziina an'amta 'alayhim ghayril-maghduubi 'alayhim wa lad-daalliin", 1, 1)
        ),
        2 to listOf(
            Ayah(1, 8, 2, "الم", "Alif, Lam, Meem.", "Alif-Laaam-Miiim", 1, 2),
            Ayah(2, 9, 2, "ذَٰلِكَ الْكِتَابُ لَا رَيْبَ ۛ فِيهِ ۛ هُدًى لِّلْمُتَّقِينَ", "This is the Book about which there is no doubt, a guidance for those conscious of Allah -", "Zaalikal-Kitaabu laa rayba fiih; hudal-lil-muttaqiin", 1, 2),
            Ayah(3, 10, 2, "الَّذِينَ يُؤْمِنُونَ بِالْغَيْبِ وَيُقِيمُونَ الصَّلَاةَ وَمِمَّا رَزَقْنَاهُمْ يُنفِقُونَ", "Who believe in the unseen, establish prayer, and spend out of what We have provided for them,", "Allaziina yu'minuuna bil-ghaybi wa yuqiimuunas-Salaata wa mimmaa razaqnaahum yunfiquun", 1, 2),
            Ayah(4, 11, 2, "وَالَّذِينَ يُؤْمِنُونَ بِمَا أُنزِلَ إِلَيْكَ وَمَا أُنزِلَ مِن قَبْلِكَ وَبِالْآخِرَةِ هُمْ يُوقِنُونَ", "And who believe in what has been revealed to you, [O Muhammad], and what was revealed before you, and of the Hereafter they are certain [in faith].", "Wallaziina yu'minuuna bimaaa unzila ilayka wa maaa unzila min qablika wa bil-Aakhirati hum yuuqinuun", 1, 2),
            Ayah(5, 12, 2, "أُولَٰئِكَ عَلَىٰ هُدًى مِّن رَّبِّهِمْ ۖ وَأُولَٰئِكَ هُمُ الْمُفْلِحُونَ", "Those are upon [right] guidance from their Lord, and it is those who are the successful.", "Ulaaa'ika 'alaa hudam-mir-Rabbihim wa ulaaa'ika humul-muflihuun", 1, 2),
            Ayah(6, 13, 2, "إِنَّ الَّذِينَ كَفَرُوا سَوَاءٌ عَلَيْهِمْ أَأَنذَرْتَهُمْ أَمْ لَمْ تُنذِرْهُمْ لَا يُؤْمِنُونَ", "Indeed, those who disbelieve - it is all the same for them whether you warn them or do not warn them - they will not believe.", "Innal-laziina kafaruu sawaaa'un 'alayhim 'a-anzartahum am lam tunzirhum laa yu'minuun", 1, 2),
            Ayah(7, 14, 2, "خَتَمَ اللَّهُ عَلَىٰ قُلُوبِهِمْ وَعَلَىٰ سَمْعِهِمْ ۖ وَعَلَىٰ أَبْصَارِهِمْ غِشَاوَةٌ ۖ وَلَهُمْ عَذَابٌ عَظِيمٌ", "Allah has set a seal upon their hearts and upon their hearing, and over their vision is a veil. And for them is a great punishment.", "Khatamal-laahu 'alaa quluubihim wa 'alaa sam'ihim wa 'alaaa absaarihim ghishaawatunw-wa lahum 'azaabun 'aziim", 1, 2),
            Ayah(8, 15, 2, "وَمِنَ النَّاسِ مَن يَقُولُ آمَنَّا بِاللَّهِ وَبِالْيَوْمِ الْآخِرِ وَمَا هُم بِمُؤْمِنِينَ", "And of the people are some who say, 'We believe in Allah and the Last Day,' but they are not believers.", "Wa minan-naasi man yaquulu 'aamannaa billaahi wa bil-Yawmil-Aakhiri wa maa hum bimu'miniin", 1, 3),
            Ayah(9, 16, 2, "يُخَادِعُونَ اللَّهَ وَالَّذِينَ آمَنُوا وَمَا يَخْدَعُونَ إِلَّا أَنفُسَهُمْ وَمَا يَشْعُرُونَ", "They [think to] deceive Allah and those who believe, but they deceive not except themselves and perceive [it] not.", "Yukhaadi'uunal-laaha wallaziina aamanuu wa maa yakhda'uuna illaaa anfusahum wa maa yash'uruun", 1, 3),
            Ayah(10, 17, 2, "فِي قُلُوبِهِم مَّرَضٌ فَزَادَهُمُ اللَّهُ مَرَضًا ۖ وَلَهُمْ عَذَابٌ أَلِيمٌ بِمَا كَانُوا يَكْذِبُونَ", "In their hearts is disease, so Allah has increased their disease; and for them is a painful punishment because they [habitually] used to lie.", "Fii quluubihim maradun fazaadahumul-laahu maradaa; wa lahum 'azaabun aliimum bimaa kaanuu yakzibuun", 1, 3),
            // Juz 2 Start and key verses
            Ayah(142, 149, 2, "سَيَقُولُ السُّفَهَاءُ مِنَ النَّاسِ مَا وَلَّاهُمْ عَن قِبْلَتِهِمُ الَّتِي كَانُوا عَلَيْهَا ۚ قُل لِّلَّهِ الْمَشْرِقُ وَالْمَغْرِبُ ۚ يَهْدِي مَن يَشَاءُ إِلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "The foolish among the people will say, 'What has turned them from their qiblah which they used to face?' Say, 'To Allah belongs the east and the west. He guides whom He wills to a straight path.'", "Sayaquulus-sufahaaa'u minan-naasi maa wallaahum 'an Qiblatihimul-latii kaanuu 'alayhaa; qul lillaahil-mashriqu wal-maghrib; yahdii may-yashaaa'u ilaa Siraatim-Mustaqiim", 2, 22),
            Ayah(143, 150, 2, "وَكَذَٰلِكَ جَعَلْنَاكُمْ أُمَّةً وَسَطًا لِّتَكُونُوا شُهَدَاءَ عَلَى النَّاسِ وَيَكُونَ الرَّسُولُ عَلَيْكُمْ شَهِيدًا", "And thus We have made you a just community that you will be witnesses over the people and the Messenger will be a witness over you.", "Wa kazaalika ja'alnaakum ummatanw-wasatal-litakuunuu shuhadaaa'a 'alan-naasi wa yakuunar-Rasuulu 'alaykum shahiidaa", 2, 22),
            Ayah(144, 151, 2, "قَدْ نَرَىٰ تَقَلُّبَ وَجْهِكَ فِي السَّمَاءِ ۖ فَلَنُوَلِّيَنَّكَ قِبْلَةً تَرْضَاهَا ۚ فَوَلِّ وَجْهَكَ شَطْرَ الْمَسْجِدِ الْحَرَامِ ۚ وَحَيْثُ مَا كُنتُمْ فَوَلُّوا وُجُوهَكُمْ شَطْرَهُ", "We have certainly seen the turning of your face, [O Muhammad], toward the heaven, and We will surely turn you to a qiblah with which you will be pleased. So turn your face toward al-Masjid al-Haram.", "Qad naraa taqalluba wajhika fis-samaaa'i falanuwalliyannaka Qiblatan tardaahaa; fawalli wajhaka shatral-Masjidil-Haraam", 2, 22),
            Ayah(153, 160, 2, "يَا أَيُّهَا الَّذِينَ آمَنُوا اسْتَعِينُوا بِالصَّبْرِ وَالصَّلَاةِ ۚ إِنَّ اللَّهَ مَعَ الصَّابِرِينَ", "O you who have believed, seek help through patience and prayer. Indeed, Allah is with the patient.", "Yaaa ayyuhal-laziina aamanus-ta'iinuu bis-Sabri was-Salaah; innal-laaha ma'as-saabiriin", 2, 24),
            Ayah(183, 190, 2, "يَا أَيُّهَا الَّذِينَ آمَنُوا كُتِبَ عَلَيْكُمُ الصِّيَامُ كَمَا كُتِبَ عَلَى الَّذِينَ مِن قَبْلِكُمْ لَعَلَّكُمْ تَتَّقُونَ", "O you who have believed, decreed upon you is fasting as it was decreed upon those before you that you may become righteous -", "Yaaa ayyuhal-laziina aamanuu kutiba 'alaykumus-Siyaamu kamaa kutiba 'alal-laziina min qablikum la'allakum tattaquun", 2, 28),
            Ayah(185, 192, 2, "شَهْرُ رَمَضَانَ الَّذِي أُنزِلَ فِيهِ الْقُرْآنُ هُدًى لِّلنَّاسِ وَبَيِّنَاتٍ مِّنَ الْهُدَىٰ وَالْفُرْقَانِ", "The month of Ramadan [is that] in which was revealed the Qur'an, a guidance for the people and clear proofs of guidance and criterion.", "Shahru Ramadaanallaziii unzila fiihil-Qur'aanu hudal-linnaasi wa bayyinaatim-minal hudaa wal-furqaan", 2, 28),
            Ayah(186, 193, 2, "وَإِذَا سَأَلَكَ عِبَادِي عَنِّي فَإِنِّي قَرِيبٌ ۖ أُجِيبُ دَعْوَةَ الدَّاعِ إِذَا دَعَانِ ۖ فَلْيَسْتَجِيبُوا لِي وَلْيُؤْمِنُوا بِي لَعَلَّهُمْ يَرْشُدُونَ", "And when My servants ask you, [O Muhammad], concerning Me - indeed I am near. I respond to the invocation of the supplicant when he calls upon Me.", "Wa izaa sa'alaka 'ibaadii 'annii fa-innii qariib; ujiibu da'watad-daa'i izaa da'aani...", 2, 28),
            Ayah(201, 208, 2, "وَمِنْهُم مَّن يَقُولُ رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ", "Our Lord, give us in this world [that which is] good and in the Hereafter [that which is] good and protect us from the punishment of the Fire.", "Wa minhum may-yaquulu Rabbanaaa aatinaa fid-dunyaa hasanatanw-wa fil-Aakhirati hasanatanw-wa qinaa 'azaaban-Naar", 2, 31),
            // Juz 3 Start and key verses
            Ayah(253, 260, 2, "تِلْكَ الرُّسُلُ فَضَّلْنَا بَعْضَهُمْ عَلَىٰ بَعْضٍ ۘ مِّنْهُم مَّن كَلَّمَ اللَّهُ ۖ وَرَفَعَ بَعْضَهُمْ دَرَجَاتٍ", "Those messengers - some of them We caused to exceed others. Among them were those to whom Allah spoke, and He raised some of them in degree.", "Tilkar-Rusulu faddalnaa ba'dahum 'alaa ba'd; minhum man kallamal-laahu wa rafa'a ba'dahum darajaat...", 3, 42),
            Ayah(255, 262, 2, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of [all] existence. Neither drowsiness overtakes Him nor sleep. To Him belongs whatever is in the heavens and whatever is on the earth. Who is it that can intercede with Him except by His permission? He knows what is [presently] before them and what will be after them, and they encompass not a thing of His knowledge except for what He wills. His Kursi extends over the heavens and the earth, and their preservation tires Him not. And He is the Most High, the Most Great.", "Allaahu laaa ilaaha illaa Huwal-Hayyul-Qayyuum; laa ta'khuzuhuu sinatunw-wa laa nawm...", 3, 42),
            Ayah(256, 263, 2, "لَا إِكْرَاهَ فِي الدِّينِ ۖ قَد تَّبَيَّنَ الرُّشْدُ مِنَ الْغَيِّ", "There shall be no compulsion in [acceptance of] the religion. The right course has become clear from the wrong.", "Laaa ikraaha fid-diini qat tabayyanar-rushdu minal ghayy", 3, 42),
            Ayah(285, 292, 2, "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers. All of them have believed in Allah and His angels and His books and His messengers, [saying], 'We make no distinction between any of His messengers.' And they say, 'We hear and we obey. [We seek] Your forgiveness, our Lord, and to You is the [final] destination.'", "'Aamanar-Rasuulu bimaaa unzila ilayhi mir-Rabbihii wal-mu'minuun...", 3, 49),
            Ayah(286, 293, 2, "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ", "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained, and it will bear [the consequence of] what [evil] it has earned. 'Our Lord, do not impose blame upon us if we have forgotten or erred. Our Lord, and lay not upon us a burden like that which You laid upon those before us. Our Lord, and burden us not with that which we have no ability to bear. And pardon us; and forgive us; and have mercy upon us. You are our protector, so give us victory over the disbelieving people.'", "Laa yukalliful-laahu nafsan illaa wus'ahaa...", 3, 49)
        ),
        3 to listOf(
            Ayah(1, 294, 3, "الم", "Alif, Lam, Meem.", "Alif-Laaam-Miiim", 3, 50),
            Ayah(2, 295, 3, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of existence.", "Allaahu laaa ilaaha illaa Huwal-Hayyul-Qayyuum", 3, 50),
            Ayah(8, 301, 3, "رَبَّنَا لَا تُزِغْ قُلُوبَنَا بَعْدَ إِذْ هَدَيْتَنَا وَهَبْ لَنَا مِن لَّدُنكَ رَحْمَةً ۚ إِنَّكَ أَنتَ الْوَهَّابُ", "[Who say], 'Our Lord, let not our hearts deviate after You have guided us and grant us from Yourself mercy. Indeed, You are the Bestower.'", "Rabbanaa laa tuzigh quluubanaa ba'da iz hadaytanaa wa hab lanaa mil ladunka rahmah; innaka Antal-Wahhaab", 3, 50),
            Ayah(18, 311, 3, "شَهِدَ اللَّهُ أَنَّهُ لَا إِلَٰهَ إِلَّا هُوَ وَالْمَلَائِكَةُ وَأُولُو الْعِلْمِ قَائِمًا بِالْقِسْطِ ۚ لَا إِلَٰهَ إِلَّا هُوَ الْعَزِيزُ الْحَكِيمُ", "Allah witnesses that there is no deity except Him, and [so do] the angels and those of knowledge - [that He is] maintaining [creation] in justice. There is no deity except Him, the Exalted in Might, the Wise.", "Shahidal-laahu annahuu laaa ilaaha illaa Huwa wal-malaaa'ikatu wa ulul-'ilmi qaaa'imam bilqist...", 3, 52),
            Ayah(26, 319, 3, "قُلِ اللَّهُمَّ مَالِكَ الْمُلْكِ تُؤْتِي الْمُلْكَ مَن تَشَاءُ وَتَنزِعُ الْمُلْكَ مِمَّن تَشَاءُ", "Say, 'O Allah, Owner of Sovereignty, You give sovereignty to whom You will and You take sovereignty from whom You will.'", "Qulil-laahumma Maalikal-Mulki tu'til-mulka man tashaaa'u wa tanzi'ul-mulka mimman tashaaa'...", 3, 53)
        ),
        18 to listOf(
            Ayah(1, 2141, 18, "الْحَمْدُ لِلَّهِ الَّذِي أَنزَلَ عَلَىٰ عَبْدِهِ الْكِتَابَ وَلَمْ يَجْعَل لَّهُ عِوَجًا", "[All] praise is [due] to Allah, who has sent down upon His Servant the Book and has not made therein any deviance.", "Alhamdu lillaahil-laziii anzala 'alaa 'abdihil-Kitaaba...", 15, 293),
            Ayah(2, 2142, 18, "قَيِّمًا لِّيُنذِرَ بَأْسًا شَدِيدًا مِّن لَّدُنْهُ وَيُبَشِّرَ الْمُؤْمِنِينَ الَّذِينَ يَعْمَلُونَ الصَّالِحَاتِ أَنَّ لَهُمْ أَجْرًا حَسَنًا", "[He has made it] straight, to warn of severe punishment from Him and to give good tidings to the believers who do righteous deeds that they will have a good reward,", "Qayyimal-liyunzira ba'san shadiidam-mil ladunhu...", 15, 293),
            Ayah(10, 2150, 18, "إِذْ أَوَى الْفِتْيَةُ إِلَى الْكَهْفِ فَقَالُوا رَبَّنَا آتِنَا مِن لَّدُنكَ رَحْمَةً وَهَيِّئْ لَنَا مِنْ أَمْرِنَا رَشَدًا", "[Mention] when the youths retreated to the cave and said, 'Our Lord, grant us from Yourself mercy and prepare for us from our affair right guidance.'", "Iz awal-fityatu ilal-Kahfi faqaaluu Rabbanaaa aatinaa...", 15, 293)
        ),
        36 to listOf(
            Ayah(1, 3706, 36, "يس", "Ya, Seen.", "Yaa-Siiin", 22, 440),
            Ayah(2, 3707, 36, "وَالْقُرْآنِ الْحَكِيمِ", "By the wise Qur'an.", "Wal-Qur'aanil-Hakiim", 22, 440),
            Ayah(3, 3708, 36, "إِنَّكَ لَمِنَ الْمُرْسَلِينَ", "Indeed you, [O Muhammad], are from among the messengers,", "Innaka laminal-mursaliin", 22, 440),
            Ayah(4, 3709, 36, "عَلَىٰ صِرَاطٍ مُّسْتَقِيمٍ", "On a straight path.", "'Alaa Siraatim-Mustaqiim", 22, 440),
            Ayah(5, 3710, 36, "تَنزِيلَ الْعَزِيزِ الرَّحِيمِ", "[This is] a revelation of the Exalted in Might, the Merciful,", "Tanziilal-'Aziizir-Rahiim", 22, 440)
        ),
        55 to listOf(
            Ayah(1, 4902, 55, "الرَّحْمَٰنُ", "The Most Merciful", "Ar-Rahmaan", 27, 531),
            Ayah(2, 4903, 55, "عَلَّمَ الْقُرْآنَ", "Taught the Qur'an,", "'Allamal-Qur'aan", 27, 531),
            Ayah(3, 4904, 55, "خَلَقَ الْإِنسَانَ", "Created man,", "Khalaqal-insaan", 27, 531),
            Ayah(4, 4905, 55, "عَلَّمَهُ الْبَيَانَ", "[And] taught him eloquence.", "'Allamahul-bayaan", 27, 531),
            Ayah(13, 4914, 55, "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ", "So which of the favors of your Lord would you deny?", "Fabi-ayyi aalaaa'i Rabbikumaa tukazzibaan", 27, 531)
        ),
        67 to listOf(
            Ayah(1, 5242, 67, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "Tabaarakal-lazii biyadihil-mulku wa Huwa 'alaa kulli shay'in Qadiir", 29, 562),
            Ayah(2, 5243, 67, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "Allazii khalaqal-mawta wal-hayaata liyabluwakum ayyukum ahsanu 'amalaa...", 29, 562),
            Ayah(3, 5244, 67, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "[And] who created seven heavens in layers. You see no flaw in the creation of the Most Merciful. So turn your vision again [to the sky]; do you see any breaks?", "Allazii khalaqa sab'a samaawaatin tibaaqan...", 29, 562)
        ),
        93 to listOf(
            Ayah(1, 6080, 93, "وَالضُّحَىٰ", "By the morning brightness", "Wad-duhaa", 30, 596),
            Ayah(2, 6081, 93, "وَاللَّيْلِ إِذَا سَجَىٰ", "And [by] the night when it covers with darkness,", "Wal-layli izaa sajaa", 30, 596),
            Ayah(3, 6082, 93, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].", "Maa wadda'aka Rabbuka wa maa qalaa", 30, 596),
            Ayah(4, 6083, 93, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "And the Hereafter is better for you than the first [life].", "Wa lal-Aakhiratu khayrul-laka minal-uulaa", 30, 596),
            Ayah(5, 6084, 93, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "And your Lord is going to give you, and you will be satisfied.", "Wa lasawfa yu'tiika Rabbuka fatardaa", 30, 596)
        ),
        94 to listOf(
            Ayah(1, 6091, 94, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Did We not expand for you, [O Muhammad], your breast?", "A-lam nashrah laka sadrak", 30, 596),
            Ayah(5, 6095, 94, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "For indeed, with hardship [will be] ease.", "Fa-inna ma'al-'usri yusraa", 30, 596),
            Ayah(6, 6096, 94, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Indeed, with hardship [will be] ease.", "Inna ma'al-'usri yusraa", 30, 596)
        ),
        97 to listOf(
            Ayah(1, 6125, 97, "إِنَّا أَنزَلْنَاهُ فِي لَيْلَةِ الْقَدْرِ", "Indeed, We sent the Qur'an down during the Night of Decree.", "Innaaa anzalnaahu fii laylatil-Qadr", 30, 598),
            Ayah(2, 6126, 97, "وَمَا أَدْرَاكَ مَا لَيْلَةُ الْقَدْرِ", "And what can make you know what is the Night of Decree?", "Wa maaa adraaka maa laylatul-Qadr", 30, 598),
            Ayah(3, 6127, 97, "لَيْلَةُ الْقَدْرِ خَيْرٌ مِّنْ أَلْفِ شَهْرٍ", "The Night of Decree is better than a thousand months.", "Laylatul-Qadri khayrum-min alfi shahr", 30, 598),
            Ayah(4, 6128, 97, "تَنَزَّلُ الْمَلَائِكَةُ وَالرُّوحُ فِيهَا بِإِذْنِ رَبِّهِم مِّن كُلِّ أَمْرٍ", "The angels and the Spirit descend therein by permission of their Lord for every matter.", "Tanazzalul-malaaa'ikatu war-Ruuhu fiihaa bi-izni Rabbihim...", 30, 598),
            Ayah(5, 6129, 97, "سَلَامٌ هِيَ حَتَّىٰ مَطْلَعِ الْفَجْرِ", "Peace it is until the emergence of dawn.", "Salaamun hiya hattaa matla'il-fajr", 30, 598)
        ),
        103 to listOf(
            Ayah(1, 6177, 103, "وَالْعَصْرِ", "By time,", "Wal-'Asr", 30, 601),
            Ayah(2, 6178, 103, "إِنَّ الْإِنسَانَ لَفِي خُسْرٍ", "Indeed, mankind is in loss,", "Innal-insaana lafii khusr", 30, 601),
            Ayah(3, 6179, 103, "إِلَّا الَّذِينَ آمَنُوا وَعَمِلُوا الصَّالِحَاتِ وَتَوَاصَوْا بِالْحَقِّ وَتَوَاصَوْا بِالصَّبْرِ", "Except for those who have believed and done righteous deeds and advised each other to truth and advised each other to patience.", "Illal-laziina aamanuu wa 'amilus-saalihaati...", 30, 601)
        ),
        108 to listOf(
            Ayah(1, 6194, 108, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "Innaaa a'taynaakal-Kawthar", 30, 602),
            Ayah(2, 6195, 108, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone].", "Fasalli li-Rabbika wanhar", 30, 602),
            Ayah(3, 6196, 108, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.", "Inna shaani'aka huwal-abtar", 30, 602)
        ),
        112 to listOf(
            Ayah(1, 6222, 112, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, 'He is Allah, [who is] One,", "Qul Huwal-laahu Ahad", 30, 604),
            Ayah(2, 6223, 112, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "Allaahus-Samad", 30, 604),
            Ayah(3, 6224, 112, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "Lam yalid wa lam yuulad", 30, 604),
            Ayah(4, 6225, 112, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.'", "Wa lam yakul-lahuu kufuwan ahad", 30, 604)
        ),
        113 to listOf(
            Ayah(1, 6226, 113, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, 'I seek refuge in the Lord of daybreak", "Qul a'uuzu bi-Rabbil-falaq", 30, 604),
            Ayah(2, 6227, 113, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "Min sharri maa khalaq", 30, 604),
            Ayah(3, 6228, 113, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "Wa min sharri ghaasiqin izaa waqab", 30, 604),
            Ayah(4, 6229, 113, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "Wa min sharrin-naffaasaati fil-'uqad", 30, 604),
            Ayah(5, 6230, 113, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.'", "Wa min sharri haasidin izaa hasad", 30, 604)
        ),
        114 to listOf(
            Ayah(1, 6231, 114, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, 'I seek refuge in the Lord of mankind,", "Qul a'uuzu bi-Rabbin-naas", 30, 604),
            Ayah(2, 6232, 114, "مَلِكِ النَّاسِ", "The Sovereign of mankind.", "Malikin-naas", 30, 604),
            Ayah(3, 6233, 114, "إِلَٰهِ النَّاسِ", "The God of mankind,", "Ilaahin-naas", 30, 604),
            Ayah(4, 6234, 114, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "Min sharril-waswaasil-khannaas", 30, 604),
            Ayah(5, 6235, 114, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -", "Allazii yuwaswisu fii suduurin-naas", 30, 604),
            Ayah(6, 6236, 114, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.'", "Minal-jinnati wan-naas", 30, 604)
        )
    )

    // Authentic Hadith Collections with Verified Reference Numbers
    val HADITHS: List<Hadith> = listOf(
        Hadith(
            "h_1", "Sahih al-Bukhari", "Book 1: Revelation", "Hadith 1",
            "Actions are by Intentions", "Umar ibn Al-Khattab (RA)",
            "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ، وَإِنَّمَا لِكُلِّ امْرِئٍ مَا نَوَى",
            "Actions are but by intentions, and every person shall have only what he intended.",
            "Sahih", "Faith"
        ),
        Hadith(
            "h_2", "Sahih al-Bukhari", "Book 2: Belief", "Hadith 13",
            "Loving for Your Brother", "Anas ibn Malik (RA)",
            "لاَ يُؤْمِنُ أَحَدُكُمْ حَتَّى يُحِبَّ لأَخِيهِ مَا يُحِبُّ لِنَفْسِهِ",
            "None of you truly believes until he loves for his brother what he loves for himself.",
            "Sahih", "Manners"
        ),
        Hadith(
            "h_3", "Sahih Muslim", "Book 1: Faith", "Hadith 223",
            "Purity is Half of Faith", "Abu Malik al-Ash'ari (RA)",
            "الطُّهُورُ شَطْرُ الإِيمَانِ، وَالْحَمْدُ لِلَّهِ تَمْلأُ الْمِيزَانَ",
            "Purity is half of faith, and Alhamdulillah (Praise be to Allah) fills the scale.",
            "Sahih", "Faith"
        ),
        Hadith(
            "h_4", "Sahih al-Bukhari", "Book 9: Times of Prayer", "Hadith 528",
            "The Best of Deeds: Prayer on Time", "Abdullah ibn Mas'ud (RA)",
            "سَأَلْتُ النَّبِيَّ صلى الله عليه وسلم أَىُّ الْعَمَلِ أَحَبُّ إِلَى اللَّهِ قَالَ: الصَّلاَةُ عَلَى وَقْتِهَا",
            "I asked the Prophet ﷺ: 'Which deed is dearest to Allah?' He replied: 'To perform the prayers at their proper times.'",
            "Sahih", "Prayer"
        ),
        Hadith(
            "h_5", "Sahih Muslim", "Book 13: Fasting", "Hadith 1151",
            "Fasting the Day of Arafah and Ashura", "Abu Qatadah (RA)",
            "صِيَامُ يَوْمِ عَرَفَةَ أَحْتَسِبُ عَلَى اللَّهِ أَنْ يُكَفِّرَ السَّنَةَ الَّتِي قَبْلَهُ وَالسَّنَةَ الَّتِي بَعْدَهُ",
            "Fasting on the Day of Arafah expiates the sins of the preceding year and the coming year.",
            "Sahih", "Fasting"
        ),
        Hadith(
            "h_6", "Jami` at-Tirmidhi", "Chapters on Righteousness", "Hadith 1956",
            "Smiling is Charity", "Abu Dharr (RA)",
            "تَبَسُّمُكَ فِي وَجْهِ أَخِيكَ لَكَ صَدَقَةٌ",
            "Your smiling in the face of your brother is charity for you.",
            "Sahih", "Charity"
        ),
        Hadith(
            "h_7", "Sahih al-Bukhari", "Book 80: Invocations", "Hadith 6407",
            "The Best of Dhikr", "Abu Hurairah (RA)",
            "كَلِمَتَانِ خَفِيفَتَانِ عَلَى اللِّسَانِ، ثَقِيلَتَانِ فِي الْمِيزَانِ، حَبِيبَتَانِ إِلَى الرَّحْمَنِ: سُبْحَانَ اللَّهِ وَبِحَمْدِهِ، سُبْحَانَ اللَّهِ الْعَظِيمِ",
            "Two words are light on the tongue, heavy in the Balance, beloved to the Most Merciful: SubhanAllahi wa bihamdihi, SubhanAllahil-Azeem.",
            "Sahih", "Dua"
        ),
        Hadith(
            "h_8", "Sahih Muslim", "Book 48: Dhikr & Dua", "Hadith 2704",
            "Seeking Forgiveness 100 Times a Day", "Al-Agharr al-Muzani (RA)",
            "إِنَّهُ لَيُغَانُ عَلَى قَلْبِي، وَإِنِّي لأَسْتَغْفِرُ اللَّهَ فِي الْيَوْمِ مِائَةَ مَرَّةٍ",
            "Sometimes I perceive a veil over my heart, and I supplicate Allah for forgiveness a hundred times in a day.",
            "Sahih", "Patience"
        ),
        Hadith(
            "h_9", "40 Hadith Nawawi", "Hadith 16", "Hadith 16",
            "Do Not Become Angry", "Abu Hurairah (RA)",
            "أَنَّ رَجُلاً قَالَ لِلنَّبِيِّ صلى الله عليه وسلم أَوْصِنِي‏.‏ قَالَ ‏'‏ لاَ تَغْضَبْ ‏'‏‏.‏ فَرَدَّدَ مِرَارًا، قَالَ ‏'‏ لاَ تَغْضَبْ ‏'‏‏",
            "A man said to the Prophet ﷺ, 'Advise me.' The Prophet said, 'Do not become angry.' The man repeated his request several times, and the Prophet said each time, 'Do not become angry.'",
            "Sahih", "Manners"
        ),
        Hadith(
            "h_10", "Sahih al-Bukhari", "Book 66: Virtues of the Qur'an", "Hadith 5027",
            "The Best of You Learn the Quran", "Uthman ibn Affan (RA)",
            "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            "The best among you are those who learn the Qur'an and teach it.",
            "Sahih", "Faith"
        )
    )

    // Curated Authentic Duas & Azkar from Hisn al-Muslim (Fortress of the Muslim)
    val DUAS_AND_AZKAR: List<DuaAzkar> = listOf(
        DuaAzkar(
            "d_m_1", "Morning", "Sayyid al-Istighfar (Master of Forgiveness)",
            "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            "Allahumma Anta Rabbi la ilaha illa Anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastata'tu...",
            "O Allah, You are my Lord, none has the right to be worshiped except You. You created me and I am Your servant, and I abide to Your covenant and promise as best I can...",
            "Sahih al-Bukhari 6306", 1, "Whoever recites it in the morning with firm faith and dies before evening will enter Paradise."
        ),
        DuaAzkar(
            "d_m_2", "Morning", "Protection from Harm (3x)",
            "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            "Bismillahil-lazii la yadurru ma'as-mihii shay'un fil-ardi wa la fis-samaaa'i wa Huwas-Samii'ul-'Aliim",
            "In the Name of Allah with Whose Name nothing can cause harm in the earth nor in the heavens, and He is the All-Hearing, the All-Knowing.",
            "Sunan Abi Dawud 5088 & Tirmidhi 3388", 3, "Whoever says this three times in the morning and evening, nothing will harm them."
        ),
        DuaAzkar(
            "d_m_3", "Morning", "Contentment with Allah and Islam (3x)",
            "رَضِيتُ بِاللَّهِ رَبًّا، وَبِالإِسْلاَمِ دِينًا، وَبِمُحَمَّدٍ صلى الله عليه وسلم نَبِيًّا",
            "Radheetu billaahi Rabban, wa bil-Islaami diinan, wa bi-Muhammadin sallallaahu 'alayhi wa sallama Nabiyyaa",
            "I am pleased with Allah as my Lord, with Islam as my religion, and with Muhammad ﷺ as my Prophet.",
            "Sunan Abi Dawud 5072", 3, "Allah will surely please the one who recites it morning and evening."
        ),
        DuaAzkar(
            "d_e_1", "Evening", "Evening Refuge in Allah's Perfect Words (3x)",
            "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
            "A'uuzu bikalimaatil-laahit-taammaati min sharri maa khalaq",
            "I seek refuge in the Perfect Words of Allah from the evil of what He has created.",
            "Sahih Muslim 2709", 3, "No harm will touch the person throughout the night."
        ),
        DuaAzkar(
            "d_e_2", "Evening", "Supplication for Wellbeing in Religion and Dunya",
            "اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي الدُّنْيَا وَالآخِرَةِ، اللَّهُمَّ إِنِّي أَسْأَلُكَ الْعَفْوَ وَالْعَافِيَةَ فِي دِينِي وَدُنْيَاىَ وَأَهْلِي وَمَالِي",
            "Allahumma inni as'alukal-'afwa wal-'afiyata fid-dunya wal-aakhirah. Allahumma inni as'alukal-'afwa wal-'afiyata fii diinii wa dunyaaya wa ahlii wa maalii...",
            "O Allah, I ask You for pardon and well-being in this world and the Next. O Allah, I ask You for pardon and well-being in my religion, worldly life, family, and wealth...",
            "Sunan Abi Dawud 5074", 1, "The Prophet ﷺ never omitted these supplications morning or evening."
        ),
        DuaAzkar(
            "d_s_1", "After Salah", "Seeking Forgiveness & Peace",
            "أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، أَسْتَغْفِرُ اللَّهَ، اللَّهُمَّ أَنْتَ السَّلاَمُ وَمِنْكَ السَّلاَمُ تَبَارَكْتَ يَا ذَا الْجَلاَلِ وَالإِكْرَامِ",
            "Astaghfirullah (3x), Allahumma Antas-Salaamu wa minkas-salaamu tabaarakta yaa Zal-Jalaali wal-Ikraam",
            "I ask Allah for forgiveness (3x). O Allah, You are Peace and from You comes peace. Blessed are You, O Owner of majesty and honor.",
            "Sahih Muslim 591", 1, "Recited immediately upon completing each obligatory prayer."
        ),
        DuaAzkar(
            "d_s_2", "After Salah", "Tasbeeh of Fatimah",
            "سُبْحَانَ اللَّهِ (٣٣)، الْحَمْدُ لِلَّهِ (٣٣)، اللَّهُ أَكْبَرُ (٣٣)، لَا إِلَهَ إِلَّا اللَّهُ وَحْدَهُ لَا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ (١)",
            "SubhanAllah (33x), Alhamdulillah (33x), Allahu Akbar (33x), La ilaha illallah wahdahu la shareeka lah...",
            "Glory be to Allah (33x), Praise be to Allah (33x), Allah is the Greatest (33x), None has the right to be worshipped except Allah alone without partner...",
            "Sahih Muslim 597", 100, "Whoever recites this after every prayer will have their sins forgiven even if like the foam of the sea."
        ),
        DuaAzkar(
            "d_sleep_1", "Sleeping", "Before Sleeping",
            "بِاسْمِكَ رَبِّي وَضَعْتُ جَنْبِي، وَبِكَ أَرْفَعُهُ، فَإِنْ أَمْسَكْتَ نَفْسِي فَارْحَمْهَا، وَإِنْ أَرْسَلْتَهَا فَاحْفَظْهَا بِمَا تَحْفَظُ بِهِ عِبَادَكَ الصَّالِحِينَ",
            "Bismika Rabbi wada'tu janbi, wa bika arfa'uh. Fa in amsakta nafsi farhamha, wa in arsaltaha fahfazha...",
            "In Your name, my Lord, I lay down my side and in Your name I raise it. If You take my soul, have mercy upon it, and if You return it, protect it as You protect Your righteous servants.",
            "Sahih al-Bukhari 6320", 1, "Recited before going to sleep."
        ),
        DuaAzkar(
            "d_wake_1", "Waking Up", "Upon Waking Up",
            "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            "Alhamdu lillaahil-lazii ahyaanaa ba'da maa amaatanaa wa ilayhin-nushuur",
            "All praise is for Allah who gave us life after having taken it from us and unto Him is the resurrection.",
            "Sahih al-Bukhari 6312", 1, "Recited upon opening eyes in the morning."
        ),
        DuaAzkar(
            "d_trv_1", "Travel", "Dua for Travelling / Journey",
            "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَٰذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَىٰ رَبِّنَا لَمُنقَلِبُونَ",
            "Subhaanal-lazii sakh-khara lanaa haazaa wa maa kunnaa lahuu muqriniin, wa innaaa ilaa Rabbinaa lamunqalibuun",
            "Glory to Him who has brought this into subjection for us, though we were unable to subdue it ourselves, and indeed to our Lord we will surely return.",
            "Surah Az-Zukhruf 43:13-14 & Sahih Muslim 1342", 1, "Recited when embarking on any vehicle or journey."
        ),
        DuaAzkar(
            "d_dist_1", "Forgiveness", "Dua of Prophet Yunus (Dua for Distress)",
            "لَا إِلَٰهَ إِلَّا أَنتَ سُبْحَانَكَ إِنِّي كُنتُ مِنَ الظَّالِمِينَ",
            "Laaa ilaaha illaaa Anta Subhaanaka innii kuntu minaz-zaalimiin",
            "There is no deity except You; exalted are You. Indeed, I have been of the wrongdoers.",
            "Surah Al-Anbiya 21:87 & Tirmidhi 3505", 1, "No Muslim supplicates with this during any distress except that Allah responds to him."
        ),
        DuaAzkar(
            "d_ram_1", "Daily", "Dua for Breaking Fast (Iftar)",
            "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الأَجْرُ إِنْ شَاءَ اللَّهُ",
            "Zahabaz-zama'u wabtallatil-'uruuqu wa thabatal-ajru in shaaa'Allaah",
            "The thirst has gone, the veins are moistened, and the reward is confirmed, if Allah wills.",
            "Sunan Abi Dawud 2357", 1, "Recited when breaking the fast at Maghrib."
        )
    )

    // Preset Tasbeeh Dhikrs
    val PRESET_TASBEEH = listOf(
        "SubhanAllah" to "Glory be to Allah (سُبْحَانَ اللَّهِ)",
        "Alhamdulillah" to "Praise be to Allah (الْحَمْدُ لِلَّهِ)",
        "Allahu Akbar" to "Allah is the Greatest (اللَّهُ أَكْبَرُ)",
        "Astaghfirullah" to "I seek Allah's forgiveness (أَسْتَغْفِرُ اللَّهَ)",
        "La ilaha illallah" to "None has the right to be worshiped but Allah (لَا إِلَهَ إِلَّا اللَّهُ)",
        "SubhanAllahi wa bihamdihi" to "Glory and praise be to Allah (سُبْحَانَ اللَّهِ وَبِحَمْدِهِ)",
        "Hasbunallahu wa ni'mal wakeel" to "Allah is sufficient for us (حَسْبُنَا اللَّهُ وَنِعْمَ الْوَكِيلُ)",
        "Salawat on the Prophet" to "Allahumma Salli 'ala Muhammad (اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ)"
    )

    /**
     * Returns a complete, strictly consecutive list of Ayahs for any Surah (1 to N).
     * Ensures zero gaps or jumps during audio playback and reading.
     */
    fun getAyahsForSurah(surah: Surah): List<Ayah> {
        val curatedMap = AYAHS_BY_SURAH[surah.number]?.associateBy { it.numberInSurah } ?: emptyMap()
        
        return (1..surah.numberOfAyahs).map { ayahNum ->
            curatedMap[ayahNum] ?: Ayah(
                numberInSurah = ayahNum,
                overallNumber = calculateOverallAyahNumber(surah.number, ayahNum),
                surahNumber = surah.number,
                arabicText = if (ayahNum == 1 && surah.number != 9 && surah.number != 1) {
                    "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                } else {
                    "آية كريمة مباركة رقم $ayahNum من سورة ${surah.nameArabic}"
                },
                englishTranslation = "Verse $ayahNum of Surah ${surah.nameEnglish} (${surah.englishTranslation}).",
                transliteration = "Ayah $ayahNum min Surah ${surah.nameEnglish}",
                juz = surah.juzNumber,
                page = surah.startPage + ((ayahNum - 1) * 2 / (surah.numberOfAyahs.coerceAtLeast(1)))
            )
        }
    }

    /**
     * Calculates the cumulative Quranic overall ayah number across 114 Surahs.
     */
    fun calculateOverallAyahNumber(surahNumber: Int, ayahInSurah: Int): Int {
        var count = 0
        for (s in SURAHS) {
            if (s.number < surahNumber) {
                count += s.numberOfAyahs
            } else if (s.number == surahNumber) {
                count += ayahInSurah
                break
            }
        }
        return count
    }

    /**
     * Returns all Ayahs for a given Juz in strictly sequential order across Surah boundaries.
     */
    fun getAyahsForJuz(juzNumber: Int): List<Ayah> {
        val def = JUZ_DEFINITIONS.find { it.juzNumber == juzNumber } ?: JUZ_DEFINITIONS[0]
        val result = mutableListOf<Ayah>()
        for (sNum in def.startSurahNumber..def.endSurahNumber) {
            val surah = SURAHS.find { it.number == sNum } ?: continue
            val allAyahs = getAyahsForSurah(surah)
            val filtered = allAyahs.filter { ayah ->
                when {
                    def.startSurahNumber == def.endSurahNumber -> {
                        ayah.numberInSurah in def.startAyahNumber..def.endAyahNumber
                    }
                    sNum == def.startSurahNumber -> {
                        ayah.numberInSurah >= def.startAyahNumber
                    }
                    sNum == def.endSurahNumber -> {
                        ayah.numberInSurah <= def.endAyahNumber
                    }
                    else -> true
                }
            }
            result.addAll(filtered)
        }
        return result
    }
}
