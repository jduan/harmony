package com.harmony

import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class Harmony {
    fun run(coordinate: MavenCoordinate) {
        downloadSource(coordinate)
        downloadPOM(coordinate)
    }

    private fun downloadPOM(coordinate: MavenCoordinate) {
        TODO("Not yet implemented")
    }

    private fun downloadSource(coordinate: MavenCoordinate) {
        val downloadUrl = SOURCE_JAR_URL.replace("GROUP_ID", coordinate.getGroupUrl())
            .replace("ARTIFACT_ID", coordinate.artifactId)
            .replace("VERSION", coordinate.version)

        val file = download(downloadUrl, coordinate.toString(), ".jar")
        println("file: $file")
    }

    private fun download(url: String, name: String, suffix: String): File {
        val inputStream = URL(url).openStream()
        val outputFile = Files.createTempFile(name, suffix)
        Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING)
        return outputFile.toFile()
    }

    companion object {
        val SOURCE_JAR_URL = "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION-sources.jar"
        val POM_URL = "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION.pom"
    }
}
