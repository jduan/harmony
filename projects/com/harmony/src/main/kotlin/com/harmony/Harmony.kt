package com.harmony

import com.google.common.io.ByteStreams
import com.harmony.pom.POM
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.jar.JarArchiveInputStream
import org.apache.commons.compress.utils.CloseShieldFilterInputStream
import java.io.File
import java.io.FileNotFoundException
import java.util.LinkedList


class Harmony(private val coordinate: MavenCoordinate, private val sourceDir: File) {
    fun run() {
        val queue = LinkedList<MavenCoordinate>()
        queue.add(coordinate)
        val visited = mutableListOf<MavenCoordinate>()
        val allProjects = AllProjects()

        while (queue.isNotEmpty()) {
            val coord = queue.pop()
            if (coord in visited) {
                continue
            }
            println("handling coord: $coord")
            visited.add(coord)
            val projectDir = downloadSource(coord) ?: continue
            val pom = POM.downloadPOM(coord)
            pom.dependencies?.forEach { dep ->
                // Ignore dependencies that have a "test" scope
                if (!dep.isTestDependency()) {
                    queue.add(dep.toMavenCoordinate(pom))
                    println("added to queue: ${dep.toMavenCoordinate(pom)}")
                }
            }

            allProjects.add(Project(projectDir, pom))
        }

        allProjects.getAll().forEach { project ->
            project.pom.dependencies?.forEach { dep ->
                val projectDir = dep.toProjectDir(sourceDir)
                val depProject = allProjects.find(projectDir)
                if (depProject != null) {
                    project.addDependency(depProject)
                }
            }
        }

        allProjects.getAll().forEach { project ->
            project.persistBuildGradle()
        }
    }

    private fun downloadSource(coordinate: MavenCoordinate): File? {
        val downloadUrl = SOURCE_JAR_URL
            .replace("GROUP_ID", coordinate.getGroupUrl())
            .replace("ARTIFACT_ID", coordinate.artifactId)
            .replace("VERSION", coordinate.version)

        val downloadUrl2 = SOURCE_JAR_URL_BACKUP
            .replace("GROUP_ID", coordinate.getGroupUrl())
            .replace("ARTIFACT_ID", coordinate.artifactId)
            .replace("VERSION", coordinate.version)

        val file = try {
            Downloader.download(downloadUrl, coordinate.toString(), ".jar")
        } catch (ex: FileNotFoundException) {
            try {
                Downloader.download(downloadUrl2, coordinate.toString(), ".jar")
            } catch (ex: FileNotFoundException) {
                null
            }
        }
        if (file == null) {
            println("Failed to download source jar for $coordinate, ignoring")
            return null
        }
        println("file: $file")

        val projectDir = sourceDir
            .resolve(coordinate.getGroupUrl())
            .resolve(coordinate.artifactId)
        unpackTarball(file, projectDir)

        return projectDir
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

    companion object {
        const val SOURCE_JAR_URL =
            "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION-sources.jar"
        // Some very old sources jars are named differently
        const val SOURCE_JAR_URL_BACKUP =
            "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION-src.jar"
    }
}
