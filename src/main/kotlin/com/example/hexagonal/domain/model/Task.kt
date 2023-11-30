package com.example.hexagonal.domain.model

import java.util.*

@JvmInline
value class TaskDescription(val value: String) {
    init {
        // eine IllegalArgumentException auslösen, wenn taskDescription leer ist
        require(value.isNotBlank()) { "Die Aufgabenbeschreibung darf nicht null oder leer sein." }
    }

    companion object {
        operator fun invoke(value: String?): TaskDescription {
            // eine IllegalArgumentException auslösen, wenn taskDescription leer ist
            requireNotNull(value) { "Die Aufgabenbeschreibung darf nicht null sein." }

            return TaskDescription(value)
        }
    }
}

@JvmInline
value class TaskPriority(val value: Int = 0) {
    init {
        // eine IllegalArgumentException auslösen, wenn taskPriority kleiner 1 ist
        require(value >= 0) { "Die Priorität darf nicht negativ sein." }
    }

    companion object {
        operator fun invoke(value: Int?): TaskPriority {
            // eine IllegalArgumentException auslösen, wenn taskPriority null ist
            requireNotNull(value) { "Die Priorität darf nicht null sein." }
            // eine IllegalArgumentException auslösen, wenn taskPriority kleiner 1 ist
            require(value >= 0) { "Die Priorität darf nicht negativ sein." }

            return TaskPriority(value)
        }
    }
}

class Task(
    val taskId: UUID = UUID.randomUUID(),
    val description: TaskDescription,
    var priority: TaskPriority = TaskPriority(0),
    var isCompleted: Boolean = false,
    var assignedUser: User? = null
)
