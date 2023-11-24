package com.example.hexagonal.domain.application.user

import com.example.hexagonal.adapter.`in`.model.UserRequestDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.application.UserService
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.out.TransactionalPort
import com.example.hexagonal.port.out.UserRepositoryPort
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CreateUserTest {

    @MockK
    private var userRepositoryPort: UserRepositoryPort = mockk<UserRepositoryPort>()

    @MockK
    private var transactionalPort: TransactionalPort= mockk<TransactionalPort>()

    @InjectMockKs
    private var userService: UserService = UserService(userRepositoryPort, transactionalPort)

    private val defaultUserRole: UserRole = UserRole.TEAM_MEMBER

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepositoryPort)
        every { transactionalPort.beginTransaction() } just Runs
        every { transactionalPort.rollbackTransaction() } just Runs
    }

    @Test
    fun `createUser should return user when successful`() {
        // Arrange
        val userName = "john.doe"
        val userRole = UserRole.MANAGER

        every { userRepositoryPort.saveUser(any()) } returns UserEntity("john.", userRole, mutableListOf<TaskEntity>())
        every { transactionalPort.commitTransaction() } just Runs
        every { userRepositoryPort.getUserByUsername("john.") } returns null

        // Act
        val result = userService.createUser(UserRequestDto(userName, userRole))

        // Assert
        assertNotNull(result)
        assertEquals("john.", result?.userName)
        assertEquals(userRole, result?.role)
        verify(exactly = 1) { userRepositoryPort.saveUser(any()) }
    }

    @Test
    fun `createUser should increment username extension and return the username with the updated extension when successful`() {
        // Arrange
        val userName = "john.doe"
        val userRole = UserRole.MANAGER
        val newUsername = "john.1"

        every { userRepositoryPort.saveUser(any()) } returns UserEntity(newUsername, userRole, mutableListOf<TaskEntity>())
        every { transactionalPort.commitTransaction() } just Runs
        every { userRepositoryPort.getUserByUsername("john.") } returns UserEntity(
            userName,
            userRole,
            mutableListOf<TaskEntity>()
        )
        every { userRepositoryPort.getUserByUsername("john.1") } returns null // Replace null with the expected result

        // Act
        val result = userService.createUser(UserRequestDto(userName, userRole))

        // Assert
        assertNotNull(result)
        assertEquals(newUsername, result?.userName)
        assertEquals(userRole, result?.role)
        verify(exactly = 1) { userRepositoryPort.saveUser(any()) }
    }

    @Test
    fun `createUser should set default role when role is null`() {
        // Arrange
        val userName = "john.doe"

        every { userRepositoryPort.saveUser(any()) } returns UserEntity(userName, UserRole.TEAM_MEMBER, mutableListOf<TaskEntity>())
        every { transactionalPort.commitTransaction() } just Runs
        every { userRepositoryPort.getUserByUsername("john.") } returns null

        // Act
        val result = userService.createUser(UserRequestDto(userName, null))

        // Assert
        assertNotNull(result)
        assertEquals(UserRole.TEAM_MEMBER, result?.role)
        verify(exactly = 1) { userRepositoryPort.saveUser(any()) }
    }

    @Test
    fun `createUser should throw IllegalArgumentException when userName is blank`() {
        // Arrange
        val userName = ""

        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(UserRequestDto(userName, defaultUserRole))
        }
        verify(exactly = 0) { userRepositoryPort.saveUser(any()) }
    }

    @Test
    fun `createUser should throw IllegalArgumentException when userName is null`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(UserRequestDto(null, defaultUserRole))
        }
        verify(exactly = 0) { userRepositoryPort.saveUser(any()) }
    }
    @Test
    fun `createUser should throw IllegalArgumentException when userRequestDto is null`() {
        // Act & Assert
        assertThrows(IllegalArgumentException::class.java) {
            userService.createUser(null)
        }
        verify(exactly = 0) { userRepositoryPort.saveUser(any()) }
    }

    @Test
    fun `createUser should rollback transaction and rethrow exception when an error occurs`() {
        // Arrange
        val userName = "john.doe"

        every { userRepositoryPort.saveUser(any()) } throws RuntimeException("Mocked exception")
        every { transactionalPort.rollbackTransaction() } just Runs

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            userService.createUser(UserRequestDto(userName, defaultUserRole))
        }
        verify(exactly = 1) { transactionalPort.rollbackTransaction() }
    }
}