package com.example.hexagonal.adapter.out.persistence.project

import com.example.hexagonal.adapter.out.persistence.ProjectRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class GetProjectByIdTest {
    @Test
    fun `getProjectById should handle non-existing project`() {
        // Arrange
        val projectRepository = ProjectRepositoryAdapter()
        val projectId = UUID.randomUUID()

        // Act
        val result = projectRepository.getProjectById(projectId)

        // Assert
        assertEquals(null, result)
    }

    @Test
    fun `getProjectById should return the project for an existing project`() {
        // Arrange
        val projectRepository = ProjectRepositoryAdapter()
        val projectId = UUID.randomUUID()
        val project = ProjectEntity(projectId, "Project 1", mutableListOf())
        projectRepository.saveProject(project)

        // Act
        val result = projectRepository.getProjectById(projectId)

        // Assert
        assertEquals(project, result)
    }

}