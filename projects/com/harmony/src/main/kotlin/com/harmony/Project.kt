package com.harmony

import com.harmony.pom.POM
import java.io.File

class Project(val projectDir: File, val pom: POM) {
    private val dependencies = mutableListOf<Project>()

    fun addDependency(depProject: Project) {
        dependencies.add(depProject)
    }

    fun persistBuildGradle() {
        val depStr = dependencies.joinToString(separator = "\n    ") {
            "compile project(':${it.toColonStr()}')"
        }
        val content = """
plugins {
    id 'java-library'
}

dependencies {
    DEPENDENCIES
}
        """.trimIndent().replace("DEPENDENCIES", depStr)

        projectDir.resolve("build.gradle").writeText(content)
    }

    private fun toColonStr(): String {
        return projectDir.path.replace("/", ":")
    }
}
