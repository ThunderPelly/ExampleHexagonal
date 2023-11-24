package com.example.hexagonal.adapter.out.persistence.user

import com.example.hexagonal.adapter.out.persistence.UserRepositoryAdapter
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.domain.model.UserRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AllUsersTest {
    @Test
    fun `allUsers should return all users in the repository`() {
        // Arrange
        val userRepository = UserRepositoryAdapter()
        val user1 = UserEntity("user1", UserRole.TEAM_MEMBER, mutableListOf())
        val user2 = UserEntity("user2", UserRole.MANAGER, mutableListOf())

        // Act
        userRepository.saveUser(user1)
        userRepository.saveUser(user2)

        // Assert
        assertEquals(2, userRepository.allUsers.size)
        assertEquals(user1, userRepository.allUsers[0])
        assertEquals(user2, userRepository.allUsers[1])
    }
}