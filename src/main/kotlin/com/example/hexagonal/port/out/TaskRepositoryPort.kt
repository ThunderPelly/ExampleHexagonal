package com.example.hexagonal.port.out

import com.example.hexagonal.adapter.out.persistence.model.TaskEntity

interface TaskRepositoryPort {
    fun saveTask(task: TaskEntity): TaskEntity
    val allTasks: List<TaskEntity>
}