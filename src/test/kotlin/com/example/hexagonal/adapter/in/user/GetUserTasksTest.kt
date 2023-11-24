package com.example.hexagonal.adapter.`in`.user

import com.example.hexagonal.adapter.`in`.UserControllerAdapter
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.UserUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class GetUserTasksTest {
    private val userUseCase: UserUseCase = mockk()
    private val userController = UserControllerAdapter(userUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(userController).build()

    @Test
    fun `getUserTasks should return a list of tasks for a user`() {
        // Arrange
        val userName = "john.doe"
        val tasks = listOf(TaskResponseDto("Task 1", false, 0), TaskResponseDto("Task 2",false, 0))
        every { userUseCase.getUserTasks(userName) } returns tasks

        // Act & Assert
        performGetUserTasksRequest(userName)
            .andExpect(jsonPath("$[0].description").value(tasks[0].description))
            .andExpect(jsonPath("$[1].description").value(tasks[1].description))
    }

    @Test
    fun `getUserTasks should handle an empty list of tasks for a user`() {
        // Arrange
        val userName = "jane.smith"
        every { userUseCase.getUserTasks(userName) } returns emptyList()

        // Act & Assert
        performGetUserTasksRequest(userName).andExpect(jsonPath("$").isEmpty)
    }

    @Test
    fun `getUserTasks should handle for a user with no tasks`() {
        // Arrange
        val userName = "nonexistent.user"
        every { userUseCase.getUserTasks(userName) } returns null

        // Act & Assert
        performGetUserTasksRequest(userName).andExpect(jsonPath("$").doesNotExist())
    }

    private fun performGetUserTasksRequest(userName: String): ResultActions {
        return mockMvc.perform(
            get("/api/v1/user/task")
                .param("userName", userName)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}