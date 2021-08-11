package com.harmony

import java.io.File

class AllProjects {
    private val projects = mutableListOf<Project>()

    fun add(project: Project) {
        projects.add(project)
    }

    fun getAll(): List<Project> = projects.toList()

    fun find(projectDir: File): Project? {
        return projects.find { p -> p.projectDir == projectDir }
    }
}
