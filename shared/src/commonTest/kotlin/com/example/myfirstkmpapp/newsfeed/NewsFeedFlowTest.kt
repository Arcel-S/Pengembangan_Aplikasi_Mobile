package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Unit Test untuk Flow dan Operator (Komponen Penilaian Flow 25%, Operators 20%, dan Bonus +10%).
 */
class NewsFeedFlowTest {

    private val repository = DefaultNewsRepository()
    private val manager = NewsFeedManager(repository)

    @Test
    fun testFlowEmission() = runTest {
        // Kebutuhan Fitur 1: Flow yang mensimulasikan data berita baru
        val items = repository.getNewsStream(intervalMs = 10L)
            .take(3)
            .toList()

        assertEquals(3, items.size, "Flow harus memancarkan 3 item")
        assertTrue(items[0].id.isNotEmpty(), "ID berita tidak boleh kosong")
        assertTrue(items[1].timestamp >= items[0].timestamp, "Timestamp berita harus meningkat")
    }

    @Test
    fun testFlowFilterOperator() = runTest {
        // Kebutuhan Fitur 2: Filter berita berdasarkan kategori tertentu
        val targetCategory = "Technology"
        val filteredList = manager.createNewsFeedFlow(
            categoryFilter = targetCategory,
            intervalMs = 10L
        ).take(3).toList()

        assertEquals(3, filteredList.size)
        filteredList.forEach { displayItem ->
            assertEquals(
                targetCategory,
                displayItem.rawItem.category,
                "Semua item harus memiliki kategori $targetCategory"
            )
        }
    }

    @Test
    fun testFlowMapTransformation() = runTest {
        // Kebutuhan Fitur 3: Transform data menjadi format yang ditampilkan
        val firstDisplayItem = manager.createNewsFeedFlow(
            categoryFilter = "ALL",
            intervalMs = 10L
        ).first()

        assertTrue(firstDisplayItem.formattedTitle.startsWith("["), "Judul hasil map harus memiliki tag format")
        assertTrue(firstDisplayItem.categoryBadge.isNotEmpty(), "Category badge tidak boleh kosong")
        assertTrue(firstDisplayItem.sourceText.startsWith("Sumber:"), "Source text harus diformat")
    }

    @Test
    fun testFlowOnEachSideEffect() = runTest {
        // Rubrik Penilaian: onEach tepat
        manager.createNewsFeedFlow(categoryFilter = "ALL", intervalMs = 10L)
            .take(2)
            .toList()

        val logs = manager.processingLogs.value
        assertTrue(logs.isNotEmpty(), "Operator onEach harus mencatat log pemrosesan")
    }

    @Test
    fun testFlowCatchErrorHandlingBonus() = runTest {
        // KOMPONEN BONUS (+10%): Implementasi error handling dengan .catch
        val itemsWithFallback = manager.createNewsFeedFlow(
            categoryFilter = "ALL",
            intervalMs = 10L,
            simulateErrorAtCount = 2
        ).take(3).toList()

        // Harus berhasil mengambil item tanpa crash karena exception ditangkap oleh .catch
        assertTrue(itemsWithFallback.isNotEmpty())
        val fallbackItem = itemsWithFallback.last()

        assertEquals("fallback-error", fallbackItem.id, "Item terakhir harus merupakan fallback dari penanganan .catch")
        assertNotNull(manager.lastError.value, "State lastError harus menyimpan pesan error yang tertangkap")
    }
}
