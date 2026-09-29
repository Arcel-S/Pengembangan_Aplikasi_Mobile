package com.example.myfirstkmpapp

import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.unit.Density
import org.jetbrains.skia.EncodedImageFormat
import java.io.File

fun main() {
    val dir1 = File("resources/screenshots").canonicalFile
    val dir2 = File("../resources/screenshots").canonicalFile
    val targetDirs = listOf(dir1, dir2).distinctBy { it.absolutePath }
    for (d in targetDirs) {
        if (!d.exists()) {
            d.mkdirs()
        }
    }

    val width = 412
    val height = 820
    val density = Density(1f)

    println("Generating screenshots for My Profile App...")

    // 1. Screenshot Tampilan Default (Halaman profil lengkap)
    captureAndSave(
        width = width,
        height = height,
        density = density,
        targetDirs = targetDirs,
        filename = "01_profile_default.png"
    ) {
        App()
    }

    // 2. Screenshot Fitur Bonus: Detail Akademik Terbuka (AnimatedVisibility)
    // Scroll ke posisi yang menampilkan detail akademik & skill chips secara optimal
    captureAndSave(
        width = width,
        height = height,
        density = density,
        targetDirs = targetDirs,
        filename = "02_profile_expanded.png"
    ) {
        App(
            initialAcademicDetailsVisible = true,
            initialScrollPosition = 300
        )
    }

    // 3. Screenshot Interaksi: Dialog Popup "Hubungi"
    captureAndSave(
        width = width,
        height = height,
        density = density,
        targetDirs = targetDirs,
        filename = "03_profile_dialog.png"
    ) {
        App(initialContactDialogVisible = true)
    }

    println("Finished generating all screenshots in: ${targetDirs.map { it.absolutePath }}")
}

private fun captureAndSave(
    width: Int,
    height: Int,
    density: Density,
    targetDirs: List<File>,
    filename: String,
    content: @androidx.compose.runtime.Composable () -> Unit
) {
    val scene = ImageComposeScene(
        width = width,
        height = height,
        density = density
    ) {
        content()
    }

    try {
        // Advance frames agar animasi transisi selesai me-render
        for (ms in listOf(0L, 100L, 250L, 500L, 800L, 1000L)) {
            scene.render(ms * 1_000_000L)
        }
        val image = scene.render(1200L * 1_000_000L)
        val data = image.encodeToData(EncodedImageFormat.PNG)

        if (data != null) {
            val bytes = data.bytes
            for (dir in targetDirs) {
                val file = File(dir, filename)
                file.writeBytes(bytes)
                println("✓ Disimpan ke ${file.absolutePath} (${file.length()} bytes)")
            }
        } else {
            System.err.println("✗ Gagal melakukan encoding PNG untuk $filename")
        }
    } catch (e: Exception) {
        System.err.println("✗ Error rendering scene: ${e.message}")
        e.printStackTrace()
    } finally {
        scene.close()
    }
}
