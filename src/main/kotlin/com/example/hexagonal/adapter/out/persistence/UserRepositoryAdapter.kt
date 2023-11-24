package com.example.hexagonal.adapter.out.persistence

import com.example.hexagonal.common.PersistenceAdapter
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.port.out.UserRepositoryPort

@PersistenceAdapter
class UserRepositoryAdapter : UserRepositoryPort {
    private var users: MutableList<UserEntity> = ArrayList()

    override fun getUserByUsername(username: String?): UserEntity? = users.find { it.userName == username }

    @Synchronized
    override fun saveUser(userEntity: UserEntity): UserEntity {
        users.add(userEntity)
        return userEntity
    }

    override val allUsers: List<UserEntity>
        get() = users
}