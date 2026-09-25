package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit Test untuk Coroutines (Komponen Penilaian Coroutines Usage 20% dan Bonus +10%).
 */
class NewsFeedCoroutinesTest {

    private val repository = DefaultNewsRepository()
    private val manager = NewsFeedManager(repository)

    @Test
    fun testFetchNewsDetailAsync() = runTest {
        // Kebutuhan Fitur 5: Coroutines untuk mengambil detail berita secara async
        val result = manager.getNewsDetailAsync(this, "news-1")

        assertTrue(result.isSuccess, "Pemanggilan async detail berita harus berhasil")
        val detail = result.getOrNull()
        assertNotNull(detail)
        assertEquals("news-1", detail.newsId)
        assertTrue(detail.content.isNotEmpty())
        assertTrue(detail.author.isNotEmpty())

        // Memverifikasi bahwa membaca detail otomatis menambah hitungan StateFlow
        assertEquals(1, manager.readCount.value, "StateFlow counter harus bertambah saat detail dibaca")
    }

    @Test
    fun testParallelAsyncAwait() = runTest {
        // Menguji eksekusi paralel coroutine async/await (Hands-on Practice 1)
        val newsIds = listOf("news-1", "news-2", "news-3")
        val details = manager.loadParallelDetails(newsIds)

        assertEquals(3, details.size, "Harus mengembalikan 3 detail berita")
        assertEquals("news-1", details[0].newsId)
        assertEquals("news-2", details[1].newsId)
        assertEquals("news-3", details[2].newsId)
    }
}
