package com.example.hexagonal.adapter.`in`.project

import com.example.hexagonal.adapter.`in`.ProjectControllerAdapter
import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.`in`.ProjectUseCase
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
import java.util.*

class CreateProjectTest {
    private val projectUseCase: ProjectUseCase = mockk()
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(ProjectControllerAdapter(projectUseCase)).build()

    @Test
    fun `createProject should return created project`() {
        // Arrange
        val projectId = UUID.randomUUID()
        val projectName = "Test Project"
        val project = ProjectResponseDto(projectId, projectName, mutableListOf())
        every { projectUseCase.createProject(ProjectRequestDto(projectName, UserRole.MANAGER)) } returns project

        val requestDto = ProjectRequestDto(name = projectName, role = UserRole.MANAGER)
        val requestBody = ObjectMapper().writeValueAsString(requestDto)

        // Act & Assert
        performCreateProjectRequest(requestBody).andExpect(status().isOk)
    }


    @Test
    fun `createProject should handle null project response`() {
        // Arrange
        val projectName = "Test Project"

        // Simulating a scenario where project creation fails
        every { projectUseCase.createProject(ProjectRequestDto(projectName, UserRole.MANAGER)) } returns null

        val requestDto = ProjectRequestDto(name = projectName, role = UserRole.MANAGER)
        val requestBody = ObjectMapper().writeValueAsString(requestDto)

        // Act & Assert
        performCreateProjectRequest(requestBody)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectId").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
    }

    private fun performCreateProjectRequest(requestBody: String): ResultActions {
        return mockMvc.perform(
            post("/api/v1/project")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}