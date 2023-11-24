package com.example.hexagonal.domain.model

import java.util.*

data class Project(val projectId: UUID = UUID.randomUUID(), val name: String) {
    val tasks: MutableList<Task> = mutableListOf()
}