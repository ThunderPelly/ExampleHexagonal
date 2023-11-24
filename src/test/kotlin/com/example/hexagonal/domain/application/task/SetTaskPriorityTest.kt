package com.example.hexagonal.domain.application.task

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.domain.application.TaskService
import com.example.hexagonal.port.out.TaskRepositoryPort
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class SetTaskPriorityTest {
    private val taskRepositoryPort: TaskRepositoryPort = mockk(relaxed = true)
    private val taskService = TaskService(taskRepositoryPort)

    @Test
    fun `setTaskPriority should update task priority and return the updated task`() {
        // Arrange
        val taskDescription = "Task1"
        val updatedPriority = 3
        val taskEntity = TaskEntity(taskDescription, false, null, 0)
        val taskResponseDto = TaskResponseDto(taskDescription, false, 3)
        every { taskRepositoryPort.allTasks } returns listOf(taskEntity)
        every { taskRepositoryPort.saveTask(any()) } returns taskEntity

        // Act
        val result = taskService.setTaskPriority(taskDescription, updatedPriority)

        // Assert
        assertEquals(taskResponseDto, result)
        assertEquals(updatedPriority, result?.priority)
        verify(exactly = 1) { taskRepositoryPort.saveTask(taskEntity) }
    }

    @Test
    fun `setTaskPriority should return null when task with given description is not found`() {
        // Arrange
        val taskDescription = "NonExistentTask"
        val priority = 2
        every { taskRepositoryPort.allTasks } returns emptyList()

        // Act
        val result = taskService.setTaskPriority(taskDescription, priority)

        // Assert
        assertNull(result)
    }

    @Test
    fun `setTaskPriority should throw IllegalArgumentException when description is null`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.setTaskPriority(null, 0)
        }

        // Assert that taskRepository.saveTask was not called
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `setTaskPriority should throw IllegalArgumentException when description is empty`() {
        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.setTaskPriority("", 0)
        }

        // Assert that taskRepository.saveTask was not called
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `setTaskPriority should throw IllegalArgumentException when priority is negative`() {
        // Arrange
        val taskDescription = "Task2"

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            taskService.setTaskPriority(taskDescription, -1)
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `setTaskPriority should throw IllegalArgumentException when priority is null`() {
        val taskDescription = "Task2"
        val priority = null

        // Act & Assert
        assertThrows<IllegalArgumentException> {
            taskService.setTaskPriority(taskDescription, priority)
        }

        // Assert that taskRepository.saveTask was not called
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }
}