package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit Test untuk StateFlow (Komponen Penilaian StateFlow Implementation 20%).
 */
class NewsFeedStateFlowTest {

    private val manager = NewsFeedManager()

    @Test
    fun testInitialStateFlowCountIsZero() = runTest {
        // StateFlow harus selalu memiliki nilai awal (Hands-on Practice 3)
        val initialCount = manager.readCount.value
        assertEquals(0, initialCount, "Nilai awal counter berita dibaca harus 0")
        assertEquals(0, manager.readCount.first())
    }

    @Test
    fun testMarkAsReadIncrementsCount() = runTest {
        // Menguji pertambahan counter saat berita dibaca
        manager.markAsRead("news-1")
        assertEquals(1, manager.readCount.value)

        manager.markAsRead("news-2")
        assertEquals(2, manager.readCount.value)
    }

    @Test
    fun testMarkAsReadDoesNotDuplicateCount() = runTest {
        // Membaca berita yang sama tidak boleh menaikkan counter ganda
        manager.markAsRead("news-1")
        manager.markAsRead("news-1")
        manager.markAsRead("news-1")

        assertEquals(1, manager.readCount.value, "Membaca ID yang sama tidak boleh menduplikasi counter")
    }

    @Test
    fun testResetReadCount() = runTest {
        // Menguji reset counter kembali ke 0
        manager.markAsRead("news-1")
        manager.markAsRead("news-2")
        assertEquals(2, manager.readCount.value)

        manager.resetReadCount()
        assertEquals(0, manager.readCount.value, "Counter harus kembali ke 0 setelah direset")
    }
}
