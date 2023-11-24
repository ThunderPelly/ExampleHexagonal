package com.example.hexagonal.adapter.`in`.model

import java.util.*

data class ProjectResponseDto(val projectId: UUID?, val name: String?, val tasks: MutableList<TaskResponseDto>?) {
    constructor() : this(null, null, mutableListOf())
}