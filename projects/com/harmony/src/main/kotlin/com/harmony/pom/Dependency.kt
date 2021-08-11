package com.harmony.pom

import com.harmony.MavenCoordinate
import com.harmony.MavenUtil

data class Dependency(val groupId: String, val artifactId: String, val version: String?) {
    fun toMavenCoordinate(): MavenCoordinate {
        // TODO: handle version that's defined in a parent POM file
        val ver = if (version == null || version.startsWith("$")) {
            MavenUtil.getLatestVersion(groupId, artifactId)
        } else {
            version
        }
        return MavenCoordinate(groupId, artifactId, ver)
    }
}
