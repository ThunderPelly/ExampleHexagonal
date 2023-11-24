package com.example.hexagonal.adapter.out.persistence.model

data class TaskEntity(val description: String, var isCompleted: Boolean, var assignedUser: UserEntity?, var priority: Int)