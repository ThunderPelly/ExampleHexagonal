package com.example.hexagonal.domain.application.task

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.domain.application.TaskService
import com.example.hexagonal.port.out.TaskRepositoryPort
import io.mockk.Called
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateTaskTest {

    private val taskRepositoryPort: TaskRepositoryPort = mockk(relaxed = true)
    private val taskService = TaskService(taskRepositoryPort)

    @BeforeEach
    fun setUp() {
        clearMocks(taskRepositoryPort)
    }

    @Test
    fun `createTask should save task when description is valid`() {
        // Arrange
        val description = "Task Description"

        // Act
        taskService.createTask(TaskRequestDto(description))

        // Assert
        verify(exactly = 1) { taskRepositoryPort.saveTask(match { it.description == description }) }
    }

    @Test
    fun `createTask should throw IllegalArgumentException when description is blank`() {
        // Arrange
        val description = ""

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.createTask(TaskRequestDto(description))
        }

        // Assert that taskRepository.saveTask was not called
        verify { taskRepositoryPort wasNot Called }
    }

    @Test
    fun `createTask should throw IllegalArgumentException when description is null`() {
        // Arrange
        val description: String? = null

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.createTask(TaskRequestDto(description))
        }

        // Assert that taskRepository.saveTask was not called
        verify { taskRepositoryPort wasNot Called }
    }

    @Test
    fun `createTask should throw IllegalArgumentException when taskRequestDto is null`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.createTask(null)
        }

        // Assert that taskRepository.saveTask was not called
        verify { taskRepositoryPort wasNot Called }
    }
}