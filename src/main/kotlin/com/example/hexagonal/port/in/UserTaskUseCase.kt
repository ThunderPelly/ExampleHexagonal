package com.example.hexagonal.port.`in`

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto

fun interface UserTaskUseCase {
    fun assignTaskToUser(userName: String?, taskRequestDto: TaskRequestDto?): TaskResponseDto?
}