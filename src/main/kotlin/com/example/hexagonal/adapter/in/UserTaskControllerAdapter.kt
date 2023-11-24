package com.example.hexagonal.adapter.`in`

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.UserTaskUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/user-task")
class UserTaskControllerAdapter(private val userTaskUseCase: UserTaskUseCase) {

    @PostMapping()
    fun assignTaskToUser(@RequestParam userName: String, @RequestBody task: TaskRequestDto): TaskResponseDto? =
        userTaskUseCase.assignTaskToUser(userName, task)
}