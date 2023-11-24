package com.example.hexagonal.domain.application.project

import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.domain.application.ProjectService
import com.example.hexagonal.domain.application.exceptions.InsufficientPermissionException
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.out.ProjectRepositoryPort
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateProjectTest {

    private val projectRepositoryPort: ProjectRepositoryPort = mockk(relaxed = true)
    private val projectService = ProjectService(projectRepositoryPort)

    @BeforeEach
    fun setUp() {
        clearMocks(projectRepositoryPort)
    }

    @Test
    fun `createProject should return ProjectResponseDto when the user has the required role`() {
        // Arrange
        val projectRequestDto = ProjectRequestDto("ProjectName", UserRole.MANAGER)

        // Act
        val result = projectService.createProject(projectRequestDto)

        // Assert
        assertEquals("ProjectName", result.name)
        verify(exactly = 1) { projectRepositoryPort.saveProject(any()) }
    }

    @Test
    fun `createProject should throw InsufficientPermissionException when the user does not have the required role`() {
        // Arrange
        val projectRequestDto = ProjectRequestDto("ProjectName", UserRole.TEAM_MEMBER)

        // Act & Assert
        assertThrows(InsufficientPermissionException::class.java) {
            projectService.createProject(projectRequestDto)
        }

        // Assert
        verify(exactly = 0) { projectRepositoryPort.saveProject(any()) }
    }

    @Test
    fun `createProject should throw IllegalArgumentException when project name is blank`() {
        // Arrange
        val projectRequestDto = ProjectRequestDto("", UserRole.MANAGER)

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.createProject(projectRequestDto)
        }

        // Assert
        verify(exactly = 0) { projectRepositoryPort.saveProject(any()) }
    }
    @Test
    fun `createProject should throw IllegalArgumentException when project name is null`() {
        // Arrange
        val projectRequestDto = ProjectRequestDto(null, UserRole.MANAGER)

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.createProject(projectRequestDto)
        }

        // Assert
        verify(exactly = 0) { projectRepositoryPort.saveProject(any()) }
    }

    @Test
    fun `createProject should throw IllegalArgumentException when projectRequestDto is null`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            projectService.createProject(null)
        }
        verify(exactly = 0) { projectRepositoryPort.saveProject(any()) }
    }
}
