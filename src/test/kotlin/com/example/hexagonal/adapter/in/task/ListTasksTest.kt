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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ListTasksTest {
    private val taskUseCase: TaskUseCase = mockk()
    private val taskController = TaskControllerAdapter(taskUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(taskController).build()

    @Test
    fun `listTasks should return a list of task descriptions`() {
        // Arrange
        val task1 = TaskResponseDto("Task 1", false, 0)
        val task2 = TaskResponseDto("Task 2", false, 0)
        every { taskUseCase.getAllTasks() } returns listOf(task1, task2)

        // Act & Assert
        performListTasksRequest()
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].description").value("Task 1"))
            .andExpect(jsonPath("$[1].description").value("Task 2"))
    }

    @Test
    fun `listTasks should return an empty list`() {
        // Arrange
        every { taskUseCase.getAllTasks() } returns emptyList()

        // Act & Assert
        performListTasksRequest()
            .andExpect(status().isOk)
            .andExpect(jsonPath("$").isEmpty)
    }

    private fun performListTasksRequest(): ResultActions {
        return mockMvc.perform(
            get("/api/v1/task")
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}