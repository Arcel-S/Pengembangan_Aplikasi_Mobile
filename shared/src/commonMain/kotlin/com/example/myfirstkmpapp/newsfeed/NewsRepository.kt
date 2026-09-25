package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * Interface repositori berita untuk abstraksi sumber data.
 */
interface NewsRepository {
    /**
     * Kebutuhan Fitur 1: Flow yang mensimulasikan data berita baru setiap 2 detik.
     * @param intervalMs Interval waktu emisi dalam milidetik (default: 2000 ms).
     * @param simulateErrorAtCount Jika bernilai > 0, akan melempar exception pada emisi ke-n untuk uji coba .catch.
     */
    fun getNewsStream(intervalMs: Long = 2000L, simulateErrorAtCount: Int = -1): Flow<NewsItem>

    /**
     * Kebutuhan Fitur 5: Coroutines untuk mengambil detail berita secara async.
     */
    suspend fun fetchNewsDetail(newsId: String): NewsDetail

    /**
     * Mengambil beberapa detail berita sekaligus secara paralel dengan async/await.
     * Mengimplementasikan pola Hands-on Practice 1 dari materi coroutines.
     */
    suspend fun fetchMultipleNewsDetailsParallel(newsIds: List<String>): List<NewsDetail>
}

class DefaultNewsRepository : NewsRepository {

    // Koleksi berita sampel dengan beragam kategori untuk diuji pada operator filter
    private val sampleNewsList = listOf(
        NewsItem(
            id = "news-1",
            title = "Kotlin 2.0 Resmi Dirilis dengan Compiler K2 yang Lebih Cepat",
            category = "Technology",
            summary = "JetBrains mengumumkan rilis stabil Kotlin 2.0 yang membawa peningkatan kecepatan kompilasi hingga 2x lipat.",
            timestamp = 1716000000000L,
            source = "TechCrunch"
        ),
        NewsItem(
            id = "news-2",
            title = "Teleskop James Webb Temukan Galaksi Tertua di Alam Semesta",
            category = "Science",
            summary = "Para astronom mendeteksi galaksi yang terbentuk hanya 300 juta tahun setelah peristiwa Big Bang.",
            timestamp = 1716002000000L,
            source = "NASA / Nature"
        ),
        NewsItem(
            id = "news-3",
            title = "Piala Dunia 2026: Format 48 Tim Siap Digelar di Tiga Negara",
            category = "Sports",
            summary = "FIFA mengonfirmasi kesiapan stadion di AS, Kanada, dan Meksiko untuk turnamen sepak bola akbar mendatang.",
            timestamp = 1716004000000L,
            source = "ESPN"
        ),
        NewsItem(
            id = "news-4",
            title = "Compose Multiplatform 1.6 Mendukung Target Web dan iOS Lebih Mulus",
            category = "Technology",
            summary = "Pengembangan aplikasi lintas platform kini semakin efisien dengan rendering skia murni dan performa native.",
            timestamp = 1716006000000L,
            source = "Kotlin Foundation"
        ),
        NewsItem(
            id = "news-5",
            title = "Inovasi Baterai Solid-State Menjanjikan Jarak Tempuh EV Hingga 1.200 KM",
            category = "Science",
            summary = "Riset terbaru material elektrolit padat berhasil menekan risiko degradasi dan panas berlebih pada sel baterai.",
            timestamp = 1716008000000L,
            source = "MIT Technology Review"
        ),
        NewsItem(
            id = "news-6",
            title = "Pasar Saham Global Menguat Ditopang Sektor Teknologi dan AI",
            category = "Business",
            summary = "Indeks bursa utama mencatat reli positif seiring meningkatnya adopsi kecerdasan buatan di sektor manufaktur.",
            timestamp = 1716010000000L,
            source = "Bloomberg"
        ),
        NewsItem(
            id = "news-7",
            title = "Timnas Bulutangkis Raih Gelar Juara di Turnamen All England",
            category = "Sports",
            summary = "Kemenangan dramatis di babak final memastikan gelar juara ganda putra bagi skuad Indonesia.",
            timestamp = 1716012000000L,
            source = "Badminton World"
        ),
        NewsItem(
            id = "news-8",
            title = "Startup Teknologi Ramah Lingkungan Raih Pendanaan Seri B 50 Juta Dolar",
            category = "Business",
            summary = "Solusi daur ulang berbasis kecerdasan buatan menarik minat investor global dalam upaya pengurangan jejak karbon.",
            timestamp = 1716014000000L,
            source = "Forbes"
        )
    )

    override fun getNewsStream(intervalMs: Long, simulateErrorAtCount: Int): Flow<NewsItem> = flow {
        var count = 0
        var index = 0

        while (true) {
            delay(intervalMs)
            count++

            if (simulateErrorAtCount > 0 && count >= simulateErrorAtCount) {
                throw IllegalStateException("Simulasi kegagalan koneksi: Aliran berita terputus pada item ke-$count")
            }

            val item = sampleNewsList[index % sampleNewsList.size].copy(
                id = "stream-$count-${itemOrFallbackId(index)}",
                timestamp = 1716000000000L + (count * 2000L)
            )
            emit(item)
            index++
        }
    }

    private fun itemOrFallbackId(index: Int): String {
        return sampleNewsList[index % sampleNewsList.size].id
    }

    override suspend fun fetchNewsDetail(newsId: String): NewsDetail = withContext(Dispatchers.Default) {
        val startTime = 100L // simulasi timer
        // Simulasi network latency
        delay(600L)

        // Cari item yang relevan atau fallback
        val baseItem = sampleNewsList.find { newsId.contains(it.id) }
            ?: sampleNewsList.first()

        NewsDetail(
            newsId = newsId,
            title = baseItem.title,
            content = "Artikel Lengkap ($newsId):\n\n${baseItem.summary}\n\n" +
                    "Informasi ini diambil secara asynchronous menggunakan Coroutines (async/await) dengan background dispatcher agar tidak memblokir thread antarmuka pengguna pada aplikasi.",
            author = "Redaksi ${baseItem.source}",
            readTimeMinutes = 3,
            fetchDurationMs = 600L
        )
    }

    override suspend fun fetchMultipleNewsDetailsParallel(newsIds: List<String>): List<NewsDetail> = coroutineScope {
        // Menggunakan async/await untuk mengambil banyak detail berita secara paralel
        val deferredList = newsIds.map { id ->
            async(Dispatchers.Default) {
                fetchNewsDetail(id)
            }
        }
        deferredList.awaitAll()
    }
}
