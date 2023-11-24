package com.example.hexagonal.adapter.out.persistence.project

import com.example.hexagonal.adapter.out.persistence.ProjectRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class AllProjectsTest {
    @Test
    fun `allProjects should return all projects in the repository`() {
        // Arrange
        val projectRepository = ProjectRepositoryAdapter()
        val project1 = ProjectEntity(UUID.randomUUID(), "Project 1", mutableListOf())
        val project2 = ProjectEntity(UUID.randomUUID(), "Project 2", mutableListOf())
        projectRepository.saveProject(project1)
        projectRepository.saveProject(project2)

        // Act
        val result = projectRepository.allProjects

        // Assert
        assertEquals(2, result.size)
        assertEquals(project1, result[project1.projectId])
        assertEquals(project2, result[project2.projectId])
    }
}