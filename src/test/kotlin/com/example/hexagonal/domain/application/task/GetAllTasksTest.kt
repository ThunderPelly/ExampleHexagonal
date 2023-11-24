package com.example.hexagonal.domain.application.task

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.domain.application.TaskService
import com.example.hexagonal.port.out.TaskRepositoryPort
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetAllTasksTest {
    private val taskRepositoryPort: TaskRepositoryPort = mockk(relaxed = true)
    private val taskService = TaskService(taskRepositoryPort)

    @Test
    fun `getAllTasks should return empty list when no tasks are present`() {
        // Arrange
        val emptyTaskList = emptyList<TaskEntity>()
        every { taskRepositoryPort.allTasks } returns emptyTaskList

        // Act
        val result = taskService.getAllTasks()

        // Assert
        assertEquals(emptyTaskList, result)
    }

    @Test
    fun `getAllTasks should return a list of tasks when tasks are present`() {
        // Arrange
        val taskEntities = listOf(TaskEntity("Task1", false, null, 0), TaskEntity("Task2", false, null, 0))
        val taskResponseDtos = listOf(TaskResponseDto("Task1", false, 0), TaskResponseDto("Task2", false,  0))
        every { taskRepositoryPort.allTasks } returns taskEntities

        // Act
        val result = taskService.getAllTasks()

        // Assert
        assertEquals(taskResponseDtos, result)
    }
}