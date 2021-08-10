package com.harmony

data class MavenCoordinate(val groupId: String, val artifactId: String, val version: String) {
    fun getGroupUrl(): String {
        return groupId.replace(".", "/")
    }

    override fun toString(): String {
        return "${groupId}_${artifactId}_${version}"
            .replace(":", "_")
            .replace("-", "_")
    }
}
