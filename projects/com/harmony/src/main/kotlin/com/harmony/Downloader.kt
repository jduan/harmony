package com.harmony

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object Downloader {
    fun download(url: String, name: String, suffix: String): File {
        val inputStream = URL(url).openStream()
        val outputFile = Files.createTempFile(name, suffix)
        Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING)
        return outputFile.toFile()
    }
}
