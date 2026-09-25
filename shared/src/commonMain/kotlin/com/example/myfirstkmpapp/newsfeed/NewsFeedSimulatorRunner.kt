package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Runner simulasi berbasis konsol/log untuk memverifikasi alur tugas secara langsung
 * persis seperti contoh Hands-on Practice pada Slide 23-28.
 */
object NewsFeedSimulatorRunner {

    fun runConsoleSimulation() = runBlocking {
        println("==================================================")
        println("  NEWS FEED SIMULATOR - TUGAS PRAKTIKUM 2")
        println("  Advanced Kotlin, Coroutines, dan Flow")
        println("==================================================")

        val manager = NewsFeedManager()

        // 1. Memantau StateFlow di background coroutine (Hands-on 3)
        val stateFlowJob = launch(Dispatchers.Default) {
            manager.readCount.collect { count ->
                println("[StateFlow Update] Total berita sudah dibaca: $count")
            }
        }

        println("\n>>> [1] Menjalankan Flow Simulasi Berita (Emit tiap 2 detik, filter: Technology, take 3)...")
        val startTime = System.currentTimeMillis()

        // Mengambil 3 berita berkategori Technology
        manager.createNewsFeedFlow(categoryFilter = "Technology", intervalMs = 500L)
            .take(3)
            .collect { displayItem ->
                println("\n🗞️  Diterima: ${displayItem.formattedTitle}")
                println("    Kategori: ${displayItem.categoryBadge} | ${displayItem.sourceText}")
                println("    Ringkasan: ${displayItem.summaryPreview}")

                // Simulasi pembacaan detail berita secara async (Hands-on 1 & Fitur 5)
                println("    ⏳ Mengambil detail berita secara async...")
                val result = manager.getNewsDetailAsync(this, displayItem.id)
                result.onSuccess { detail ->
                    println("    ✅ Detail berhasil diambil (${detail.fetchDurationMs}ms): ${detail.author}")
                }
            }

        val duration = System.currentTimeMillis() - startTime
        println("\n>>> Selesai streaming dalam ${duration}ms.")

        // 2. Demonstrasi paralel async/await
        println("\n>>> [2] Uji Coba Eksekusi Paralel (async/await untuk 2 artikel)...")
        val parallelStart = System.currentTimeMillis()
        val details = manager.loadParallelDetails(listOf("news-1", "news-2"))
        val parallelEnd = System.currentTimeMillis()
        println("    Berhasil mengambil ${details.size} artikel secara paralel dalam ${parallelEnd - parallelStart}ms")

        // 3. Demonstrasi error handling .catch
        println("\n>>> [3] Uji Coba Error Handling Flow (.catch)...")
        manager.createNewsFeedFlow(categoryFilter = "ALL", intervalMs = 100L, simulateErrorAtCount = 2)
            .take(3)
            .collect { item ->
                println("    Item: ${item.formattedTitle}")
            }

        stateFlowJob.cancel()
        println("\n==================================================")
        println("  SIMULASI SELESAI DENGAN SUKSES!")
        println("==================================================")
    }
}
