package com.example.hexagonal.adapter.`in`.task

import com.example.hexagonal.adapter.`in`.TaskControllerAdapter
import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.TaskUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class CreateTaskTest {
    private val taskUseCase: TaskUseCase = mockk()
    private val taskController = TaskControllerAdapter(taskUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(taskController).build()

    @Test
    fun `createTask should return 200 when a task is created successfully`() {
        // Arrange
        val taskDescription = "New Task"
        every { taskUseCase.createTask(TaskRequestDto(taskDescription)) } returns TaskResponseDto(taskDescription, false, 0)

        // Act & Assert
        performCreateTaskRequest(TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value(taskDescription))
    }

    @Test
    fun `createTask should handle when the task description is blank`() {
        // Arrange
        val taskDescription = ""
        every { taskUseCase.createTask(TaskRequestDto(taskDescription)) } returns null

        // Act & Assert
        performCreateTaskRequest(TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    @Test
    fun `createTask should handle when the task description is null`() {
        // Arrange
        val taskDescription: String? = null
        every { taskUseCase.createTask(TaskRequestDto(taskDescription)) } returns null

        // Act & Assert
        performCreateTaskRequest(TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    private fun performCreateTaskRequest(taskRequestDto: TaskRequestDto): ResultActions {
        return mockMvc.perform(
            post("/api/v1/task")
                .content(ObjectMapper().writeValueAsString(taskRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}