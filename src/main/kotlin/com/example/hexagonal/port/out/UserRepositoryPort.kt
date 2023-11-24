package com.example.hexagonal.port.out

import com.example.hexagonal.adapter.out.persistence.model.UserEntity


interface UserRepositoryPort {
    fun saveUser(userEntity: UserEntity): UserEntity
    fun getUserByUsername(username: String?): UserEntity?
    val allUsers: List<UserEntity>
}