package com.example.hexagonal.domain.model

import java.util.*

@JvmInline
value class ProjectName private constructor(val value: String) {
    init {
        // eine IllegalArgumentException auslösen, wenn projectName leer ist
        require(value.isNotBlank()) { "Der Projektname darf nicht leer sein." }
    }

    companion object {
        operator fun invoke(value: String?): ProjectName {
            // eine IllegalArgumentException auslösen, wenn projectName leer ist
            requireNotNull(value) { "Der Projektname darf nicht null sein." }

            return ProjectName(value)
        }
    }
}

data class Project(val projectId: UUID = UUID.randomUUID(), val name: ProjectName) {
    val tasks: MutableList<Task> = mutableListOf()
}