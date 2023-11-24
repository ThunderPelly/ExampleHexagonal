package com.example.hexagonal.adapter.`in`

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.TaskUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/task")
class TaskControllerAdapter(private val taskUseCase: TaskUseCase) {

    @PostMapping()
    fun createTask(@RequestBody taskRequestDto: TaskRequestDto): TaskResponseDto? =
        taskUseCase.createTask(taskRequestDto)

    @GetMapping()
    fun listTasks(): List<TaskResponseDto>? =
        taskUseCase.getAllTasks()

    @PutMapping("/{taskDescription}/priority")
    fun setTaskPriority(
        @PathVariable taskDescription: String,
        @RequestParam priority: Int
    ): TaskResponseDto? =
       taskUseCase.setTaskPriority(taskDescription, priority)

    @PutMapping("/{taskDescription}/complete")
    fun completeTask(
        @PathVariable taskDescription: String,
    ): TaskResponseDto? =
        taskUseCase.completeTask(taskDescription)
}