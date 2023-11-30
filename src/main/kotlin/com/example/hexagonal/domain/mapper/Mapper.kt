package com.example.hexagonal.domain.mapper

import com.example.hexagonal.adapter.`in`.model.*
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.model.*

fun User.toEntity(): UserEntity =
    UserEntity(userName.value, role, assignedTasks.map { it.toEntity() }.toMutableList())

fun User.toResponseDto(): UserResponseDto =
    UserResponseDto(userName.value, role)

fun UserEntity.toDomain(): User = User(userName = UserName(userName), role = role).apply {
    assignedTasks.mapTo(assignedTasks) { it.toEntity().toDomain() }
}

fun Task.toEntity(): TaskEntity =
    TaskEntity(description.value, isCompleted, assignedUser?.toEntity(), priority.value)

fun Task.toResponseDto(): TaskResponseDto =
    TaskResponseDto(description.value, isCompleted, priority.value)

fun TaskEntity.toDomain(): Task = Task(description = TaskDescription(description), priority = TaskPriority(priority), isCompleted = isCompleted)

fun Project.toEntity(): ProjectEntity =
    ProjectEntity(projectId, name, tasks.map { it.toEntity() }.toMutableList())

fun ProjectEntity.toDomain(): Project = Project(projectId, name).also { project ->
    tasks.mapTo(project.tasks) { it.toDomain() }
}


fun Project.toResponseDto(): ProjectResponseDto =
    ProjectResponseDto(projectId, name, tasks.map { it.toResponseDto() }.toMutableList())