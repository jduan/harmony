package com.harmony.pom

import com.harmony.MavenCoordinate
import com.harmony.MavenUtil
import java.io.File

data class Dependency(val groupId: String, val artifactId: String, val version: String?) {
    fun toMavenCoordinate(pom: POM): MavenCoordinate {
        // TODO: handle version that's defined in a parent POM file
        val ver = when {
            version == null -> {
                MavenUtil.getLatestVersion(groupId, artifactId)
            }
            version.startsWith("$") -> {
                val ver = version.substring(2, version.length - 1)
                pom.properties?.get(ver) ?: MavenUtil.getLatestVersion(groupId, artifactId)
            }
            else -> {
                version
            }
        }
        return MavenCoordinate(groupId, artifactId, ver)
    }

    fun toProjectDir(sourceDir: File): File {
        return sourceDir
            .resolve(getGroupUrl())
            .resolve(artifactId)
    }

    private fun getGroupUrl(): String {
        return groupId.replace(".", "/")
    }
}
