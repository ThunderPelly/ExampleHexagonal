package com.example.hexagonal.domain.model

import java.util.*
import kotlin.collections.ArrayList


data class User(val userId: UUID = UUID.randomUUID(), val userName: String, val role: UserRole) {
    val assignedTasks: MutableList<Task> = ArrayList()
}


