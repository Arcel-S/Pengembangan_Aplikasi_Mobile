package com.example.myfirstkmpapp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "My Profile App",
        state = rememberWindowState(width = 420.dp, height = 750.dp)
    ) {
        App()
    }
}