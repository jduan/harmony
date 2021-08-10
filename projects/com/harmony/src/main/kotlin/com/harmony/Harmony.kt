package com.harmony

import com.google.common.io.ByteStreams
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.utils.CloseShieldFilterInputStream
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption


class Harmony(val coordinate: MavenCoordinate, val sourceDir: File) {
    fun run() {
        downloadSource(coordinate)
        // downloadPOM(coordinate)
    }

    private fun downloadPOM(coordinate: MavenCoordinate) {
        TODO("Not yet implemented")
    }

    private fun downloadSource(coordinate: MavenCoordinate) {
        val downloadUrl = SOURCE_JAR_URL
            .replace("GROUP_ID", coordinate.getGroupUrl())
            .replace("ARTIFACT_ID", coordinate.artifactId)
            .replace("VERSION", coordinate.version)

        val file = download(downloadUrl, coordinate.toString(), ".jar")
        println("file: $file")

        val projectDir = sourceDir
            .resolve(coordinate.getGroupUrl())
            .resolve(coordinate.artifactId)
        unpackTarball(file, projectDir)
    }

    // Unpack a tar ball of lockfiles (via a stream from an S3 object) and build a map of
    // lockfile names to DependencyLockFile objects.
    private fun unpackTarball(jarFile: File, projectDir: File) {
        val archive = JarArchiveInputStream(jarFile.inputStream())

        var entry: ArchiveEntry?
        while (true) {
            entry = archive.nextEntry
            if (entry == null) {
                break
            }
            if (!entry.isDirectory) { // we aren't interested in directories
                // We need to use CloseShieldInputStream here because otherwise "loadFrom" would
                // close the stream upon finishing reading one tar entry. CloseShieldInputStream
                // provides a proxy stream that prevents the underlying input stream from being closed.
                // val lockfile = DependencyLockfile.loadFrom(entry.name, CloseShieldInputStream(archive))
                val outputDir = projectDir.resolve("src/main/java") // TODO: don't hard code
                outputDir.mkdirs()
                val outputFile = outputDir.resolve(entry.name)
                outputFile.parentFile.mkdirs()
                outputFile.createNewFile()
                ByteStreams.copy(CloseShieldFilterInputStream(archive), outputFile.outputStream())
            }
        }
    }

    private fun download(url: String, name: String, suffix: String): File {
        val inputStream = URL(url).openStream()
        val outputFile = Files.createTempFile(name, suffix)
        Files.copy(inputStream, outputFile, StandardCopyOption.REPLACE_EXISTING)
        return outputFile.toFile()
    }

    companion object {
        val SOURCE_JAR_URL =
            "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION-sources.jar"
        val POM_URL =
            "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION.pom"
    }
}
