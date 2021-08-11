package com.harmony

import java.lang.RuntimeException
import java.net.URL

object MavenUtil {
    fun getLatestVersion(groupId: String, artifactId: String): String {
        val searchUrl =
            "http://search.maven.org/solrsearch/select?q=g:%22${groupId}%22+AND+a:%22${artifactId}%22"
        val inputStream = URL(searchUrl).openStream()
        val data = String(inputStream.readBytes())
        val regex = "latestVersion\":\"(.*?)\"".toRegex()

        return regex.find(data)?.groupValues?.get(1)
            ?: throw RuntimeException("Failed to find the latest version of ${groupId}:${artifactId}")
    }
}
