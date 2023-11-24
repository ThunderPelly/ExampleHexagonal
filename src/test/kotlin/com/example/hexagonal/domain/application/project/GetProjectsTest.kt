package com.example.hexagonal.domain.application.project

import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.domain.application.ProjectService
import com.example.hexagonal.port.out.ProjectRepositoryPort
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class GetProjectsTest {

    private val projectRepositoryPort: ProjectRepositoryPort = mockk(relaxed = true)
    private val projectService = ProjectService(projectRepositoryPort)

    @BeforeEach
    fun setUp() {
        clearMocks(projectRepositoryPort)
    }

    @Test
    fun `getProjects should return a list of ProjectResponseDto when projects exist`() {
        // Arrange
        val project1 = ProjectEntity(UUID.randomUUID(), "Project 1", mutableListOf<TaskEntity>())
        val project2 = ProjectEntity(UUID.randomUUID(), "Project 2", mutableListOf<TaskEntity>())

        every { projectRepositoryPort.allProjects } returns mutableMapOf(
            project1.projectId to project1,
            project2.projectId to project2
        )

        // Act
        val result = projectService.getProjects()

        // Assert
        assertEquals(2, result?.size)
        assertEquals(project1.projectId, result?.get(0)?.projectId)
        assertEquals(project1.name, result?.get(0)?.name)
        assertEquals(project2.projectId, result?.get(1)?.projectId)
        assertEquals(project2.name, result?.get(1)?.name)
    }

    @Test
    fun `getProjects should return an empty list when no projects exist`() {
        // Arrange
        every { projectRepositoryPort.allProjects } returns mutableMapOf()

        // Act
        val result = projectService.getProjects()

        // Assert
        assertEquals(0, result?.size)
    }
}
