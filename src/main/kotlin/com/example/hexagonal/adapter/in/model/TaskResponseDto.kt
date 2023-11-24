package com.example.hexagonal.adapter.`in`.model

data class TaskResponseDto(val description: String?, val isCompleted: Boolean?, val priority: Int?) {
    constructor() : this(null, null, null)
}
