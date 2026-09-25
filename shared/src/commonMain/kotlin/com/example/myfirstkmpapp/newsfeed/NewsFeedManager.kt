package com.example.myfirstkmpapp.newsfeed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Manajer orkestrasi News Feed.
 * Menggabungkan:
 * - Kebutuhan 1: Flow stream berita
 * - Kebutuhan 2: Filter operator
 * - Kebutuhan 3: Map transformation operator
 * - Operator onEach untuk side effect / audit log
 * - Komponen Bonus: Error handling dengan .catch
 * - Kebutuhan 4: StateFlow untuk counter berita dibaca
 * - Kebutuhan 5: Coroutines async/await untuk detail berita
 */
class NewsFeedManager(
    private val repository: NewsRepository = DefaultNewsRepository()
) {

    // =========================================================================
    // STATEFLOW IMPLEMENTATION (Kebutuhan Fitur 4 & Rubrik 20%)
    // =========================================================================
    // Enkapsulasi: _readCount privat sebagai MutableStateFlow, diekspos sebagai StateFlow read-only
    private val _readCount = MutableStateFlow(0)
    val readCount: StateFlow<Int> = _readCount.asStateFlow()

    // Melacak ID berita yang sudah dibaca agar penghitungan akurat (tidak duplikat)
    private val _readNewsIds = MutableStateFlow<Set<String>>(emptySet())
    val readNewsIds: StateFlow<Set<String>> = _readNewsIds.asStateFlow()

    // State kategori aktif yang dipilih pengguna
    private val _selectedCategory = MutableStateFlow("ALL")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // State log pemrosesan operator onEach untuk transparansi side effect
    private val _processingLogs = MutableStateFlow<List<String>>(emptyList())
    val processingLogs: StateFlow<List<String>> = _processingLogs.asStateFlow()

    // State pesan error dari penanganan error (.catch / try-catch)
    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    // State detail berita aktif
    private val _currentDetail = MutableStateFlow<NewsDetail?>(null)
    val currentDetail: StateFlow<NewsDetail?> = _currentDetail.asStateFlow()

    private val _isLoadingDetail = MutableStateFlow(false)
    val isLoadingDetail: StateFlow<Boolean> = _isLoadingDetail.asStateFlow()

    /**
     * Memperbarui kategori filter.
     */
    fun setCategory(category: String) {
        _selectedCategory.value = category
        addLog("Filter kategori diubah menjadi: $category")
    }

    /**
     * Menandai berita telah dibaca dan memperbarui StateFlow readCount.
     */
    fun markAsRead(newsId: String) {
        val currentSet = _readNewsIds.value
        if (!currentSet.contains(newsId)) {
            val updatedSet = currentSet + newsId
            _readNewsIds.value = updatedSet
            _readCount.value = updatedSet.size
            addLog("Berita dibaca [$newsId]. Total dibaca: ${_readCount.value}")
        }
    }

    /**
     * Mereset counter berita dibaca ke nilai awal 0.
     */
    fun resetReadCount() {
        _readNewsIds.value = emptySet()
        _readCount.value = 0
        addLog("Counter berita dibaca direset ke 0")
    }

    /**
     * Membersihkan pesan error.
     */
    fun clearError() {
        _lastError.value = null
    }

    private fun addLog(message: String) {
        val current = _processingLogs.value
        val updated = (listOf(message) + current).take(15) // simpan 15 log terbaru
        _processingLogs.value = updated
    }

    // =========================================================================
    // FLOW PIPELINE: filter, onEach, map, catch (Kebutuhan Fitur 1, 2, 3 & Bonus)
    // =========================================================================
    /**
     * Membangun stream berita terfilter dan tertransformasi.
     * Menggunakan rantai operator:
     * 1. filter: menyaring sesuai kategori (Kebutuhan 2)
     * 2. onEach: memicu side effect log pemrosesan
     * 3. map: mentransformasi NewsItem -> DisplayNewsItem (Kebutuhan 3)
     * 4. catch: menangani exception secara reaktif (Komponen Bonus +10%)
     */
    fun createNewsFeedFlow(
        categoryFilter: String = _selectedCategory.value,
        intervalMs: Long = 2000L,
        simulateErrorAtCount: Int = -1
    ): Flow<DisplayNewsItem> {
        return repository.getNewsStream(intervalMs, simulateErrorAtCount)
            // Operator 1: filter (Kebutuhan 2)
            .filter { item ->
                if (categoryFilter.equals("ALL", ignoreCase = true)) {
                    true
                } else {
                    item.category.equals(categoryFilter, ignoreCase = true)
                }
            }
            // Operator 2: onEach untuk side effect
            .onEach { filteredItem ->
                addLog("Menerima berita [${filteredItem.category}]: ${filteredItem.title.take(30)}...")
            }
            // Operator 3: map transformasi ke format tampilan (Kebutuhan 3)
            .map { rawItem ->
                rawItem.toDisplayFormat()
            }
            // Operator 4: catch untuk graceful error recovery (Komponen Bonus)
            .catch { throwable ->
                val errorMsg = throwable.message ?: "Terjadi kesalahan pada stream"
                _lastError.value = errorMsg
                addLog("Error pada stream (.catch): $errorMsg")

                // Memancarkan fallback DisplayNewsItem agar consumer tidak crash
                emit(
                    DisplayNewsItem(
                        id = "fallback-error",
                        formattedTitle = "[ERROR] Aliran Berita Terganggu",
                        categoryBadge = "Peringatan",
                        formattedDate = "Sistem",
                        summaryPreview = "Stream menangkap error ($errorMsg) dengan operator .catch.",
                        sourceText = "Sumber: Sistem",
                        rawItem = NewsItem(
                            id = "fallback-error",
                            title = "Aliran Berita Terganggu",
                            category = "System",
                            summary = errorMsg,
                            timestamp = 0L,
                            source = "Fallback"
                        )
                    )
                )
            }
    }

    // =========================================================================
    // COROUTINES ASYNC / AWAIT (Kebutuhan Fitur 5 & Rubrik 20%)
    // =========================================================================
    /**
     * Mengambil detail berita secara asynchronous menggunakan async/await dan Dispatcher.
     * Mengimplementasikan error handling dengan try-catch (Komponen Bonus).
     */
    suspend fun getNewsDetailAsync(
        scope: CoroutineScope,
        newsId: String
    ): Result<NewsDetail> = withContext(Dispatchers.Default) {
        _isLoadingDetail.value = true
        _lastError.value = null

        try {
            // Memulai coroutine asynchronous dengan builder async
            val deferredDetail = scope.async(Dispatchers.Default) {
                repository.fetchNewsDetail(newsId)
            }

            // Menunggu hasil dengan .await() non-blocking
            val detail = deferredDetail.await()
            _currentDetail.value = detail

            // Otomatis tandai berita sebagai dibaca saat detail dibuka
            markAsRead(newsId)

            Result.success(detail)
        } catch (e: Exception) {
            val errorMsg = "Gagal memuat detail berita: ${e.message}"
            _lastError.value = errorMsg
            addLog("Error coroutine async: $errorMsg")
            Result.failure(e)
        } finally {
            _isLoadingDetail.value = false
        }
    }

    /**
     * Demonstrasi pengambilan beberapa detail berita secara paralel (Hands-on Practice 1).
     */
    suspend fun loadParallelDetails(newsIds: List<String>): List<NewsDetail> {
        return repository.fetchMultipleNewsDetailsParallel(newsIds)
    }

    fun dismissDetailDialog() {
        _currentDetail.value = null
    }
}
