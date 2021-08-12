package com.harmony.pom

import com.harmony.MavenCoordinate
import com.harmony.MavenUtil
import java.io.File
import java.lang.RuntimeException

data class Dependency(val groupId: String, val artifactId: String, val version: String?) {
    fun toMavenCoordinate(pom: POM): MavenCoordinate {
        val gid = if (groupId.startsWith("$")) {
            pom.getProperty(groupId.substring(2, groupId.length - 1).split(".").last())
        } else {
            groupId
        }
        if (gid == null) {
            throw RuntimeException("Failed to find group id $groupId, $gid")
        }
        val aid = if (artifactId.startsWith("$")) {
            pom.getProperty(artifactId.substring(2, artifactId.length - 1).split(".").last())
        } else {
            artifactId
        }
        if (aid == null) {
            throw RuntimeException("Failed to find artifact id $artifactId")
        }

        // TODO: handle version that's defined in a parent POM file
        val ver = when {
            version == null -> {
                MavenUtil.getLatestVersion(gid, aid)
            }
            version.startsWith("$") -> {
                val ver = version.substring(2, version.length - 1)
                pom.getProperty(ver) ?: MavenUtil.getLatestVersion(gid, aid)
            }
            else -> {
                version
            }
        }
        return MavenCoordinate(gid, aid, ver)
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
