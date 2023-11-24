package com.example.hexagonal.port.`in`

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.`in`.model.UserRequestDto
import com.example.hexagonal.adapter.`in`.model.UserResponseDto

interface UserUseCase {
    fun createUser(userRequestDto: UserRequestDto?): UserResponseDto?
    fun getAllUsers(): List<UserResponseDto>?
    fun getUserTasks(userName: String?): List<TaskResponseDto>?
}