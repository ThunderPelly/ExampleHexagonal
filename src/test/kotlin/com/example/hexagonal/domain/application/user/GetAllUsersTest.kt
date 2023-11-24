package com.example.hexagonal.domain.application.user

import com.example.hexagonal.adapter.`in`.model.UserResponseDto
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

class GetAllUsersTest {

    @MockK
    private var userRepositoryPort: UserRepositoryPort = mockk<UserRepositoryPort>()

    @MockK
    private var transactionalPort: TransactionalPort = mockk<TransactionalPort>()

    @InjectMockKs
    private var userService: UserService = UserService(userRepositoryPort, transactionalPort)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        clearMocks(userRepositoryPort)
    }

    @Test
    fun `getAllUsers should return empty list when no users exist`() {
        // Arrange
        every { userRepositoryPort.allUsers } returns emptyList()

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertNotNull(result)
        result?.let { assertTrue(it.isEmpty()) }
    }

    @Test
    fun `getAllUsers should return a list of users when users exist`() {
        // Arrange
        val userEntities = listOf(UserEntity("user1", UserRole.TEAM_MEMBER, mutableListOf()), UserEntity("user2", UserRole.MANAGER, mutableListOf()))
        val expectedResult = listOf(UserResponseDto("user1", UserRole.TEAM_MEMBER), UserResponseDto("user2", UserRole.MANAGER))
        every { userRepositoryPort.allUsers } returns userEntities

        // Act
        val result = userService.getAllUsers()

        // Assert
        assertNotNull(result)
        result?.let { assertEquals(userEntities.size, it.size) }
        assertEquals(expectedResult, result)
    }
}