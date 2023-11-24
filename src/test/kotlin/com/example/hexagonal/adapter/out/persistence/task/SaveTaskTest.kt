package com.example.hexagonal.adapter.out.persistence.task

import com.example.hexagonal.adapter.out.persistence.TaskRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SaveTaskTest {
    @Test
    fun `saveTask should add a task to the repository`() {
        // Arrange
        val taskRepository = TaskRepositoryAdapter()
        val task = TaskEntity("New Task", false, null, 1)

        // Act
        val result = taskRepository.saveTask(task)

        // Assert
        assertEquals(task, result)
        assertEquals(1, taskRepository.allTasks.size)
        assertEquals(task, taskRepository.allTasks.first())
    }
}