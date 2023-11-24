package com.example.hexagonal.adapter.`in`.user

import com.example.hexagonal.adapter.`in`.UserControllerAdapter
import com.example.hexagonal.adapter.`in`.model.UserResponseDto
import com.example.hexagonal.domain.model.UserRole
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

class ListUsersTest {
    private val userUseCase: UserUseCase = mockk()
    private val userController = UserControllerAdapter(userUseCase)
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(userController).build()

    @Test
    fun `listUsers should return a list of users`() {
        // Arrange
        val users = listOf(
            UserResponseDto("john.doe", UserRole.TEAM_MEMBER),
            UserResponseDto("jane.smith", UserRole.MANAGER)
        )
        every { userUseCase.getAllUsers() } returns users

        // Act & Assert
        performListUsersRequest()
            .andExpect(jsonPath("$[0].userName").value(users[0].userName))
            .andExpect(jsonPath("$[0].role").value(users[0].role?.name))
            .andExpect(jsonPath("$[1].userName").value(users[1].userName))
            .andExpect(jsonPath("$[1].role").value(users[1].role?.name))
    }

    @Test
    fun `listUsers should handle an empty list of users`() {
        // Arrange
        every { userUseCase.getAllUsers() } returns emptyList()

        // Act & Assert
        performListUsersRequest().andExpect(jsonPath("$").isEmpty)
    }

    private fun performListUsersRequest(): ResultActions {
        return mockMvc.perform(
            get("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}