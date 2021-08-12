package com.harmony.pom

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.InputStream


data class POM(@JsonProperty("dependencies") val dependencies: List<Dependency>?,
               @JsonProperty("properties") val properties: Map<String, String>?
) {
    companion object {
        private val xmlMapper = XmlMapper().registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        fun loadFromStream(inputStream: InputStream): POM {
            return xmlMapper.readValue(inputStream, POM::class.java)
        }
    }
}
