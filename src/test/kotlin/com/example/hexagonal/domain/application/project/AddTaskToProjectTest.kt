package com.example.hexagonal.domain.application.project

import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import com.example.hexagonal.domain.application.ProjectService
import com.example.hexagonal.port.out.ProjectRepositoryPort
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.*

class AddTaskToProjectTest {
    private val projectRepositoryPort: ProjectRepositoryPort = mockk(relaxed = true)
    private val projectService = ProjectService(projectRepositoryPort)

    @Test
    fun `addTaskToProject should add a task to the project and return the updated project`() {
        // Arrange
        val projectId = UUID.randomUUID()
        val projectEntity = ProjectEntity(UUID.randomUUID(), "ProjectName", mutableListOf())
        val taskResponseDto = TaskResponseDto("newTask", false, 0)
        val projectResponseDto = ProjectResponseDto(projectEntity.projectId, projectEntity.name, mutableListOf(taskResponseDto))
        every { projectRepositoryPort.getProjectById(projectId) } returns projectEntity

        // Act
        val result = projectService.addTaskToProject(projectId, taskResponseDto.description)

        // Assert
        assertEquals(projectResponseDto, result)
        assertEquals(1, result?.tasks?.size)
        assertEquals(taskResponseDto.description, result?.tasks?.first()?.description)
    }

    @Test
    fun `addTaskToProject should handle when project with given id is not found`() {
        // Arrange
        val projectId = UUID.randomUUID()
        every { projectRepositoryPort.getProjectById(projectId) } returns null

        // Act & Assert
        assertEquals(null, projectService.addTaskToProject(projectId, "Test"))
    }

    @Test
    fun `addTaskToProject should handle when taskDescription is blank`() {
        // Arrange
        val projectId = UUID.randomUUID()

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.addTaskToProject(projectId, "")
        }
    }

    @Test
    fun `addTaskToProject should handle when taskDescription is null`() {
        // Arrange
        val projectId = UUID.randomUUID()
        
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.addTaskToProject(projectId, null)
        }
    }

    @Test
    fun `addTaskToProject should handle when projectId is null`() {
        // Arrange
        val projectId = null

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.addTaskToProject(projectId, "")
        }
    }

    @Test
    fun `addTaskToProject should handle when projectId is not valid`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.addTaskToProject(UUID.fromString("some wrong Id"), "")
        }
    }
}