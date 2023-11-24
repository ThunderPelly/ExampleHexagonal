package com.example.hexagonal.domain.model

import java.util.*

data class Task(val taskId: UUID = UUID.randomUUID(), val description: String, val priority: Int = 0, val isCompleted: Boolean = false, val assignedUser: User? = null)

