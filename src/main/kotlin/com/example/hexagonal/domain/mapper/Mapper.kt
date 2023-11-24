package com.example.hexagonal.domain.mapper

import com.example.hexagonal.adapter.`in`.model.*
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.model.Project
import com.example.hexagonal.domain.model.Task
import com.example.hexagonal.domain.model.User

fun User.toEntity(): UserEntity =
    UserEntity(userName, role, assignedTasks.map { it.toEntity() }.toMutableList())

fun User.toResponseDto(): UserResponseDto =
    UserResponseDto(userName, role)

fun UserEntity.toDomain(): User = User(userName = userName, role = role).apply {
    assignedTasks.mapTo(assignedTasks) { it.toEntity().toDomain() }
}

fun Task.toEntity(): TaskEntity =
    TaskEntity(description, isCompleted, assignedUser?.toEntity(), priority)

fun Task.toResponseDto(): TaskResponseDto =
    TaskResponseDto(description, isCompleted, priority)

fun TaskEntity.toDomain(): Task = Task(description = description, priority = priority, isCompleted = isCompleted)

fun Project.toEntity(): ProjectEntity =
    ProjectEntity(projectId, name, tasks.map { it.toEntity() }.toMutableList())

fun ProjectEntity.toDomain(): Project = Project(projectId, name).also { project ->
    tasks.mapTo(project.tasks) { it.toDomain() }
}


fun Project.toResponseDto(): ProjectResponseDto =
    ProjectResponseDto(projectId, name, tasks.map { it.toResponseDto() }.toMutableList())