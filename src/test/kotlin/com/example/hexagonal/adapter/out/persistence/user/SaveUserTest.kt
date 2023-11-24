package com.example.hexagonal.adapter.out.persistence.user

import com.example.hexagonal.adapter.out.persistence.UserRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.model.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SaveUserTest {
    @Test
    fun `saveUser should add a user to the repository`() {
        // Arrange
        val userRepository = UserRepositoryAdapter()
        val user = UserEntity("john.doe", UserRole.MANAGER, mutableListOf())

        // Act
        val result = userRepository.saveUser(user)

        // Assert
        assertEquals(user, result)
        assertEquals(1, userRepository.allUsers.size)
        assertEquals(user, userRepository.allUsers.first())
    }
}