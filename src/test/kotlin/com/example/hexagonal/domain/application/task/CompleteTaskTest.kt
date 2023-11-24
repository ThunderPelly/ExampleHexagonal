package com.example.hexagonal.domain.application.task

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.domain.application.TaskService
import com.example.hexagonal.port.out.TaskRepositoryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CompleteTaskTest {

    private val taskRepositoryPort: TaskRepositoryPort = mockk(relaxed = true)
    private val taskService = TaskService(taskRepositoryPort)

    @Test
    fun `completeTask should mark task as completed and return the updated task`() {
        // Arrange
        val taskDescription = "Task1"
        val taskEntity = TaskEntity(taskDescription, false, null, 0)
        val taskResponseDto = TaskResponseDto(taskDescription, true,  0)
        every { taskRepositoryPort.allTasks } returns listOf(taskEntity)
        every { taskRepositoryPort.saveTask(any()) } returns taskEntity

        // Act
        val result = taskService.completeTask(taskDescription)

        // Assert
        assertEquals(taskResponseDto, result)
        assertEquals(true, result?.isCompleted)
        verify(exactly = 1) { taskRepositoryPort.saveTask(taskEntity) }
    }

    @Test
    fun `completeTask should return null when task with given description is not found`() {
        // Arrange
        val taskDescription = "NonExistentTask"
        every { taskRepositoryPort.allTasks } returns emptyList()

        // Act
        val result = taskService.completeTask(taskDescription)

        // Assert
        assertNull(result)
    }

    @Test
    fun `completeTask should throw IllegalArgumentException when description is null`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.completeTask(null)
        }

        // Assert that taskRepository.saveTask was not called
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `completeTask should throw IllegalArgumentException when description is empty`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.completeTask("")
        }

        // Assert that taskRepository.saveTask was not called
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }
}