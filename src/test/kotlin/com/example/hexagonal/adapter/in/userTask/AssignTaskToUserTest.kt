package com.example.hexagonal.adapter.`in`.userTask


import com.example.hexagonal.adapter.`in`.UserTaskControllerAdapter
import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.UserTaskUseCase
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

class AssignTaskToUserTest {

    private val userTaskUseCase: UserTaskUseCase = mockk()
    private val taskController = UserTaskControllerAdapter(userTaskUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(taskController).build()

    @Test
    fun `assignTaskToUser should return updated Task when a task is assigned successfully`() {
        // Arrange
        val username = "john.doe"
        val taskDescription = "New Task"
        every { userTaskUseCase.assignTaskToUser(username, TaskRequestDto(taskDescription)) } returns TaskResponseDto(taskDescription, false, 0)

        // Act & Assert
        performAssignTaskToUserRequest(username, TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").value(taskDescription))
    }

    @Test
    fun `assignTaskToUser should handle when the task description is blank`() {
        // Arrange
        val userName = "john.doe"
        val taskDescription = ""
        every { userTaskUseCase.assignTaskToUser(userName, TaskRequestDto(taskDescription)) } returns null

        // Act & Assert
        performAssignTaskToUserRequest(userName, TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())
    }

    @Test
    fun `assignTaskToUser should handle when the task description is null`() {
        // Arrange
        val username = "john.doe"
        val taskDescription: String? = null
        every { userTaskUseCase.assignTaskToUser(username, TaskRequestDto(taskDescription)) } returns null

        // Act & Assert
        performAssignTaskToUserRequest(username, TaskRequestDto(taskDescription))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.description").doesNotExist())

    }

    private fun performAssignTaskToUserRequest(userName: String, taskRequestDto: TaskRequestDto): ResultActions {
        return mockMvc.perform(
            post("/api/v1/user-task")
                .param("userName", userName)
                .content(ObjectMapper().writeValueAsString(taskRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}