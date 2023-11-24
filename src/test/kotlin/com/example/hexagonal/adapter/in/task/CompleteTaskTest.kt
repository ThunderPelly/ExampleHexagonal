package com.example.hexagonal.adapter.`in`.task

import com.example.hexagonal.adapter.`in`.TaskControllerAdapter
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.TaskUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class CompleteTaskTest {
    private val taskUseCase: TaskUseCase = mockk()
    private val taskController = TaskControllerAdapter(taskUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(taskController).build()

    @Test
    fun `completeTask should complete the task and return the task details`() {
        // Arrange
        val taskDescription = "Existing Task"
        val completedTask = TaskResponseDto(taskDescription, false, 0)
        every { taskUseCase.completeTask(taskDescription) } returns completedTask

        // Act & Assert
        performCompleteTaskRequest(taskDescription)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value(taskDescription))
    }

    @Test
    fun `completeTask should handle when the task is not found`() {
        // Arrange
        val taskDescription = "Nonexistent Task"
        every { taskUseCase.completeTask(taskDescription) } returns null

        // Act & Assert
        performCompleteTaskRequest(taskDescription)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    private fun performCompleteTaskRequest(taskDescription: String): ResultActions {
        return mockMvc.perform(
            put("/api/v1/task/{taskDescription}/complete", taskDescription)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}