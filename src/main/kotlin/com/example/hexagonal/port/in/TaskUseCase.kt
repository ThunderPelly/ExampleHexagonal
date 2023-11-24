package com.example.hexagonal.port.`in`

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto

interface TaskUseCase {
    fun createTask(taskRequestDto: TaskRequestDto?): TaskResponseDto?
    fun getAllTasks(): List<TaskResponseDto>?
    fun setTaskPriority(description: String?, priority: Int?): TaskResponseDto?
    fun completeTask(description: String?): TaskResponseDto?
}