package com.example.hexagonal.adapter.out.persistence.task

import com.example.hexagonal.adapter.out.persistence.TaskRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AllTasksTest {
    @Test
    fun `allTasks should return all tasks in the repository`() {
        // Arrange
        val taskRepository = TaskRepositoryAdapter()
        val task1 = TaskEntity("Task 1", false, null, 1)
        val task2 = TaskEntity("Task 2", false, null, 1)

        // Act
        taskRepository.saveTask(task1)
        taskRepository.saveTask(task2)

        // Assert
        assertEquals(2, taskRepository.allTasks.size)
        assertEquals(task1, taskRepository.allTasks[0])
        assertEquals(task2, taskRepository.allTasks[1])
    }
}