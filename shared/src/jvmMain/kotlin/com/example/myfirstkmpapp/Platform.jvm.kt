package com.example.myfirstkmpapp

class JVMPlatform: Platform {
    override val name: String = "Desktop JVM"
}

actual fun getPlatform(): Platform = JVMPlatform()