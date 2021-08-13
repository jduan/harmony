package com.harmony.pom

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationConfig
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.harmony.Downloader
import com.harmony.MavenCoordinate
import java.io.FileInputStream
import java.io.InputStream


data class POM(
    @JsonProperty("groupId") val groupId: String?,
    @JsonProperty("artifactId") val artifactId: String?,
    @JsonProperty("version") val version: String?,
    @JsonProperty("dependencies") val dependencies: List<Dependency>?,
    @JsonProperty("properties") val properties: Map<String, String>?,
    @JsonProperty("parent") val parent: Map<String, String>?
) {
    var parentPOM: POM? = null

    fun getProperty(prop: String): String? {
        var value: String? = null
        if (prop == "groupId") value = groupId ?: parentPOM?.groupId
        if (prop == "artifactId") value = artifactId ?: parentPOM?.artifactId
        if (prop == "version") value = version ?: parentPOM?.version

        if (value == null && properties != null) {
            value = properties[prop]
        }
        if (value == null) {
            value = parentPOM?.properties?.get(prop)
        }

        return value
    }

    companion object {
        private val xmlMapper = XmlMapper().registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//            .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
//            .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
            .configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true)
        const val POM_URL =
            "https://repo1.maven.org/maven2/GROUP_ID/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION.pom"

        fun downloadPOM(coordinate: MavenCoordinate): POM {
            val downloadUrl = POM_URL
                .replace("GROUP_ID", coordinate.getGroupUrl())
                .replace("ARTIFACT_ID", coordinate.artifactId)
                .replace("VERSION", coordinate.version)

            val file = Downloader.download(downloadUrl, coordinate.toString(), ".pom")
            println("file: $file")
            return loadFromStream(FileInputStream(file))
        }

        fun loadFromStream(inputStream: InputStream): POM {
            val pom = xmlMapper.readValue(inputStream, POM::class.java)

            if (pom.parent != null) {
                val coord = MavenCoordinate(
                    pom.parent["groupId"]!!,
                    pom.parent["artifactId"]!!,
                    pom.parent["version"]!!
                )
                val parentPOM = downloadPOM(coord)
                pom.parentPOM = parentPOM
            }

            return pom
        }
    }
}
