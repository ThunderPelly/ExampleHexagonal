package com.example.hexagonal.domain.application.userTask

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.application.UserTaskService
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.out.TaskRepositoryPort
import com.example.hexagonal.port.out.UserRepositoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AssignTaskToUserTest {

    @MockK
    private var userRepositoryPort: UserRepositoryPort = mockk<UserRepositoryPort>()

    @MockK
    private var taskRepositoryPort: TaskRepositoryPort = mockk<TaskRepositoryPort>()

    @InjectMockKs
    private var userTaskService: UserTaskService = UserTaskService(userRepositoryPort, taskRepositoryPort)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepositoryPort)
        clearMocks(taskRepositoryPort)
    }

    @Test
    fun `assignTaskToUser should assign a task to a user successfully`() {
        // Arrange
        val userName = "john.doe"
        val user = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        val taskDescription = "New Task"
        val task = TaskEntity(taskDescription, false, null, 0)
        val allTasks = listOf(task)
        every { userRepositoryPort.getUserByUsername(userName) } returns user
        every { taskRepositoryPort.allTasks } returns allTasks
        every { taskRepositoryPort.saveTask(any()) } returns TaskEntity(taskDescription, false, user, 0)

        // Act
        val result = userTaskService.assignTaskToUser(
            userName,
            TaskRequestDto(taskDescription)
        )

        // Assert
        assertNotNull(result)
        assertEquals(TaskResponseDto(taskDescription, false, 0), result)
        assertEquals(1, user.assignedTasks.size)
    }

    @Test
    fun `assignTaskToUser should not assign a task when user does not exist`() {
        // Arrange
        val userName = "john.doe"
        val taskDescription = "New Task"

        every { userRepositoryPort.getUserByUsername(userName) } returns null
        every { taskRepositoryPort.allTasks } returns listOf(TaskEntity(taskDescription, false, null, 0))

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userTaskService.assignTaskToUser(
                userName,
                TaskRequestDto(taskDescription)
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should throw IllegalArgumentException when taskRequestDto is null`() {
        // Arrange
        val userName = "john.doe"

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userTaskService.assignTaskToUser(
                userName,
                null
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should throw IllegalArgumentException when username is null`() {
        // Arrange
        val taskDescription = "New Task"

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userTaskService.assignTaskToUser(
                null,
                TaskRequestDto(taskDescription)
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should throw IllegalArgumentException when username is empty`() {
        // Arrange
        val taskDescription = "New Task"

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userTaskService.assignTaskToUser(
                "",
                TaskRequestDto(taskDescription)
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should not assign a task when task does not exist`() {
        // Arrange
        val username = "john.doe"
        val taskDescription = "New Task"

        every { userRepositoryPort.getUserByUsername(username) } returns UserEntity(
            username,
            UserRole.TEAM_MEMBER,
            mutableListOf<TaskEntity>()
        )
        every { taskRepositoryPort.allTasks } returns emptyList()

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userTaskService.assignTaskToUser(
                username,
                TaskRequestDto(taskDescription)
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should not assign a task when task is completed`() {
        // Arrange
        val userName = "john.doe"
        val taskDescription = "New Task"
        val user = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        val task = TaskEntity(taskDescription, true, null, 0)
        task.isCompleted = true
        user.assignedTasks.add(task)

        every { userRepositoryPort.getUserByUsername(userName) } returns user
        every { taskRepositoryPort.allTasks } returns listOf(task)

        // Act & Assert
        assertThrows(IllegalStateException::class.java) {
            userTaskService.assignTaskToUser(
                userName,
                TaskRequestDto(taskDescription)
            )
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }

    @Test
    fun `assignTaskToUser should assign a task to a user with a high-priority assigned task`() {
        // Arrange
        val userName = "john.doe"
        val taskDescription = "New Task"
        val user = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        val highPrioTask = TaskEntity("Highprio Task", false, null, 4)
        val taskEntities = listOf(
            highPrioTask,
            TaskEntity(taskDescription, false, null, 0),
            TaskEntity("Task3", false, null, 0),
            TaskEntity("Task4", false, null, 0),
            TaskEntity("Task5", false, null, 0)
        )
        taskEntities.map {
            user.assignedTasks.add(it)
        }

        every { userRepositoryPort.getUserByUsername(userName) } returns user
        every { taskRepositoryPort.allTasks } returns taskEntities
        every { taskRepositoryPort.saveTask(any()) } returns TaskEntity(taskDescription, false, null, 4)

        // Act
        val result = userTaskService.assignTaskToUser(userName, TaskRequestDto(taskDescription))

        // Assert
        assertEquals(TaskResponseDto(taskDescription, false, 5), result)
        assertEquals(6, user.assignedTasks.size)
        assertEquals(taskEntities[0].priority, 4)
        assertEquals(taskEntities[0], user.assignedTasks[0])
    }

    @Test
    fun `reassignTask should not reassign a task when there are to many low prio tasks`() {
        // Arrange
        val userName = "john.doe"
        val taskDescription = "New Task"
        val user = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        val tasks = listOf(
            TaskEntity(taskDescription, false, null, 0),
            TaskEntity("Task3", false, null, 0),
            TaskEntity("Task4", false, null, 0),
            TaskEntity("Task5", false, null, 0)
        )
        tasks.map {
            user.assignedTasks.add(it)
        }

        every { userRepositoryPort.getUserByUsername(userName) } returns user
        every { taskRepositoryPort.allTasks } returns tasks

        // Act & Assert
        assertThrows(IllegalStateException::class.java) {
            userTaskService.assignTaskToUser(userName, TaskRequestDto(taskDescription))
        }
        verify(exactly = 0) { taskRepositoryPort.saveTask(any()) }
    }
}