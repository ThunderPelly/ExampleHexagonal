package com.example.hexagonal.adapter.out.persistence.model

import java.util.*

data class ProjectEntity(val projectId: UUID, val name: String, var tasks: MutableList<TaskEntity>)