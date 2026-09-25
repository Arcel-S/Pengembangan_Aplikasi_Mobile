package com.example.myfirstkmpapp.newsfeed

/**
 * Model data berita mentah yang dipancarkan oleh Flow.
 * Memenuhi kriteria data class & null safety pada materi Advanced Kotlin.
 */
data class NewsItem(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val timestamp: Long,
    val source: String,
    val imageUrl: String? = null
)

/**
 * Model tampilan hasil transformasi operator .map { ... }
 * Kebutuhan Fitur 3: Transform data menjadi format yang ditampilkan.
 */
data class DisplayNewsItem(
    val id: String,
    val formattedTitle: String,
    val categoryBadge: String,
    val formattedDate: String,
    val summaryPreview: String,
    val sourceText: String,
    val rawItem: NewsItem
)

/**
 * Model detail berita yang diambil secara asynchronous menggunakan Coroutines (async/await).
 * Kebutuhan Fitur 5: Coroutines untuk mengambil detail berita secara async.
 */
data class NewsDetail(
    val newsId: String,
    val title: String,
    val content: String,
    val author: String,
    val readTimeMinutes: Int,
    val fetchDurationMs: Long
)

/**
 * Extension functions untuk memformat berita.
 * Mengimplementasikan konsep Extension Functions dari Materi 2.
 */
fun NewsItem.toDisplayFormat(): DisplayNewsItem {
    return DisplayNewsItem(
        id = this.id,
        formattedTitle = "[${this.category.uppercase()}] ${this.title}",
        categoryBadge = this.category,
        formattedDate = "Baru saja",
        summaryPreview = if (this.summary.length > 90) "${this.summary.take(90)}..." else this.summary,
        sourceText = "Sumber: ${this.source}",
        rawItem = this
    )
}

/**
 * Extension function untuk normalisasi string nullable (Materi Null Safety & Extension Functions).
 */
fun String?.orDefault(default: String = "Umum"): String = this ?: default
