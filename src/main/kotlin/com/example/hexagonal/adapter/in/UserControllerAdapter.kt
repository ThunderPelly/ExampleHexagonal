package com.example.hexagonal.adapter.`in`

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.`in`.model.UserRequestDto
import com.example.hexagonal.adapter.`in`.model.UserResponseDto
import com.example.hexagonal.port.`in`.UserUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserControllerAdapter(private val userUseCase: UserUseCase) {

    @PostMapping()
    fun createUser(@RequestBody userRequestDto: UserRequestDto): UserResponseDto? =
        userUseCase.createUser(userRequestDto)

    @GetMapping()
    fun listUsers(): List<UserResponseDto>? =
        userUseCase.getAllUsers()

    @GetMapping("task")
    fun getUserTasks(@RequestParam userName: String): List<TaskResponseDto>? =
        userUseCase.getUserTasks(userName)
}