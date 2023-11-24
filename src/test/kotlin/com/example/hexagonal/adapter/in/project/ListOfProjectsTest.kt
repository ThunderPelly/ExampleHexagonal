package com.example.hexagonal.adapter.`in`.project

import com.example.hexagonal.adapter.`in`.ProjectControllerAdapter
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import com.example.hexagonal.port.`in`.ProjectUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class ListOfProjectsTest {
    private val projectUseCase: ProjectUseCase = mockk()
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(ProjectControllerAdapter(projectUseCase)).build()

    @Test
    fun `listProjects should return a list of ProjectResponseDto`() {
        // Arrange
        val project1 = ProjectResponseDto(UUID.randomUUID(), "Project1", mutableListOf())
        val project2 = ProjectResponseDto(UUID.randomUUID(), "Project2", mutableListOf())
        every { projectUseCase.getProjects() } returns mutableListOf(project1, project2)

        val expectedResponse = listOf(
            ProjectResponseDto(project1.projectId, project1.name, project1.tasks),
            ProjectResponseDto(project2.projectId, project2.name, project2.tasks)
        )

        // Act & Assert
        performGetListOfProjectsRequest()
            .andExpect(status().isOk)
            .andExpect(content().json(ObjectMapper().writeValueAsString(expectedResponse)))
    }

    @Test
    fun `listProjects should handle empty list response`() {
        // Arrange
        // Simulating an empty list of projects
        every { projectUseCase.getProjects() } returns mutableListOf()

        val expectedResponse = listOf<ProjectResponseDto>() // Expecting an empty list

        // Act & Assert
        performGetListOfProjectsRequest()
            .andExpect(status().isOk)
            .andExpect(content().json(ObjectMapper().writeValueAsString(expectedResponse)))
    }

    private fun performGetListOfProjectsRequest(): ResultActions {
        return mockMvc.perform(
            get("/api/v1/project")
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}