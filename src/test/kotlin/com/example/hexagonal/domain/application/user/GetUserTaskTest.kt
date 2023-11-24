package com.example.hexagonal.domain.application.user

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.application.UserService
import com.example.hexagonal.domain.application.UserTaskService
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.out.TaskRepositoryPort
import com.example.hexagonal.port.out.TransactionalPort
import com.example.hexagonal.port.out.UserRepositoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetUserTasksTest {

    @MockK
    private var userRepositoryPort: UserRepositoryPort = mockk<UserRepositoryPort>()

    @MockK
    private var transactionalPort: TransactionalPort = mockk<TransactionalPort>()

    @MockK
    private lateinit var taskRepositoryPort: TaskRepositoryPort

    @InjectMockKs
    private var userService: UserService = UserService(userRepositoryPort, transactionalPort)

    @InjectMockKs
    private lateinit var userTaskService: UserTaskService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepositoryPort)
        clearMocks(taskRepositoryPort)
        every { transactionalPort.beginTransaction() } just Runs
    }

    @Test
    fun `getUserTasks should return null when user does not exist`() {
        // Arrange
        val userName = "nonexistentUser"
        every { userRepositoryPort.getUserByUsername(userName) } returns null

        // Act
        val result = userService.getUserTasks(userName)

        // Assert
        assertNull(result)
        verify(exactly = 1) { userRepositoryPort.getUserByUsername(userName) }
    }

    @Test
    fun `getUserTasks should return empty list when user has no tasks`() {
        // Arrange
        val userName = "userWithoutTasks"
        val userEntity = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        every { userRepositoryPort.getUserByUsername(userName) } returns userEntity

        // Act
        val result = userService.getUserTasks(userName)

        // Assert
        assertNotNull(result)
        assertTrue(result!!.isEmpty())
        verify(exactly = 1) { userRepositoryPort.getUserByUsername(userName) }
    }

    @Test
    fun `getUserTasks should return a list of tasks when user has tasks`() {
        // Arrange
        val userName = "userWithTasks"
        val taskEntities = listOf(TaskEntity("Task1", false, null, 0), TaskEntity("Task2", false, null, 0))
        val userEntity = UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        val expectedResult = listOf(TaskResponseDto("Task1", false, 0), TaskResponseDto("Task2", false, 0))
        every { userRepositoryPort.getUserByUsername(userName) } returns userEntity
        every { taskRepositoryPort.allTasks } returns taskEntities
        every { taskRepositoryPort.saveTask(taskEntities.get(0)) } returns taskEntities.get(0)
        every { taskRepositoryPort.saveTask(taskEntities.get(1)) } returns taskEntities.get(1)

        assignTaskToUser(userName, taskEntities.get(0).description)
        assignTaskToUser(userName, taskEntities.get(1).description)

        // Act
        val result = userService.getUserTasks(userName)

        // Assert
        assertNotNull(result)
        assertEquals(taskEntities.size, result!!.size)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `getUserTasks should throw IllegalArgumentException when userName is null`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userService.getUserTasks(null)
        }
        verify(exactly = 0) { userRepositoryPort.getUserByUsername(any()) }
    }
    @Test
    fun `getUserTasks should throw IllegalArgumentException when userName is empty`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userService.getUserTasks("")
        }
        verify(exactly = 0) { userRepositoryPort.getUserByUsername(any()) }
    }

    private fun assignTaskToUser(username: String, taskDescription: String): TaskResponseDto? {
        return userTaskService.assignTaskToUser(username,
            TaskRequestDto(taskDescription)
        )
    }
}