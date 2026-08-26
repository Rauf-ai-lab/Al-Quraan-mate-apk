package com.example.search

enum class SearchCategory(val title: String, val badge: String) {
    ALL("All", "All"),
    QURAN("Quran & Tafsir", "Quran"),
    HADITH("Authentic Hadith", "Hadith"),
    DUA("Duas & Azkar", "Dua"),
    KNOWLEDGE("Islamic Articles", "Article"),
    FIQH("Fiqh & Rulings", "Fiqh")
}

data class SearchResultItem(
    val id: String,
    val title: String,
    val category: SearchCategory,
    val categoryBadge: String,
    val arabicText: String? = null,
    val transliteration: String? = null,
    val snippet: String,
    val fullContent: String,
    val reference: String,
    val sourceName: String,
    val webUrl: String? = null,
    val isVerified: Boolean = true
)

sealed class SearchUiState {
    object Idle : SearchUiState()
    object Loading : SearchUiState()
    data class Success(
        val query: String,
        val selectedCategory: SearchCategory,
        val results: List<SearchResultItem>,
        val totalCount: Int
    ) : SearchUiState()
    data class Empty(
        val query: String,
        val suggestion: String? = null
    ) : SearchUiState()
    data class Error(
        val query: String,
        val errorMessage: String,
        val canRetry: Boolean = true
    ) : SearchUiState()
}
