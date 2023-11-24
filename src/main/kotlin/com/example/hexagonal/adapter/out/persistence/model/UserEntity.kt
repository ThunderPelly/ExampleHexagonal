package com.example.hexagonal.adapter.out.persistence.model

import com.example.hexagonal.domain.model.UserRole

data class UserEntity(val userName: String, val role: UserRole, var assignedTasks: MutableList<TaskEntity>)