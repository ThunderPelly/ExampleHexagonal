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

class SetTaskPriorityTest {
    private val taskUseCase: TaskUseCase = mockk()
    private val taskController = TaskControllerAdapter(taskUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(taskController).build()

    @Test
    fun `setTaskPriority should set priority for the specified task`() {
        // Arrange
        val taskDescription = "Task 1"
        val priority = 3
        every { taskUseCase.setTaskPriority(taskDescription, priority) } returns TaskResponseDto(taskDescription, false, 0)

        // Act & Assert
        performSetTaskPriorityRequest(taskDescription, priority)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value(taskDescription))
    }

    @Test
    fun `setTaskPriority should handle when the task is not found`() {
        // Arrange
        val taskDescription = "Nonexistent Task"
        val priority = 3
        every { taskUseCase.setTaskPriority(taskDescription, priority) } returns null

        // Act & Assert
        performSetTaskPriorityRequest(taskDescription, priority)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    private fun performSetTaskPriorityRequest(taskDescription: String, priority: Int): ResultActions {
        return mockMvc.perform(
            put("/api/v1/task/{taskDescription}/priority", taskDescription)
                .param("priority", priority.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}