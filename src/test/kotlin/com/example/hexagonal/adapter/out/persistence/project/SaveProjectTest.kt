package com.example.hexagonal.adapter.out.persistence.project

import com.example.hexagonal.adapter.out.persistence.ProjectRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class SaveProjectTest {

    @Test
    fun `saveProject should add a project to the repository`() {
        // Arrange
        val projectRepository = ProjectRepositoryAdapter()
        val projectId = UUID.randomUUID()
        val project = ProjectEntity(projectId, "Project 1", mutableListOf())

        // Act
        val savedProject = projectRepository.saveProject(project)

        // Assert
        assertEquals(project, savedProject)
        assertEquals(project, projectRepository.getProjectById(projectId))
    }
}