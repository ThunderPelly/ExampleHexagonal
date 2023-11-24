package com.example.hexagonal.adapter.out.persistence.user

import com.example.hexagonal.adapter.out.persistence.UserRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.model.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GetUserByUsernameTest {
    @Test
    fun `getUserByUsername should return user with the given username`() {
        // Arrange
        val userRepository = UserRepositoryAdapter()
        val username = "john.doe"
        val user = UserEntity(username, UserRole.MANAGER, mutableListOf())
        userRepository.saveUser(user)

        // Act
        val result = userRepository.getUserByUsername(username)

        // Assert
        assertEquals(user, result)
    }

    @Test
    fun `getUserByUsername should handle when user is not found`() {
        // Arrange
        val userRepository = UserRepositoryAdapter()
        val username = "nonexistent.user"

        // Act
        val result = userRepository.getUserByUsername(username)

        // Assert
        assertNull(result)
    }

}