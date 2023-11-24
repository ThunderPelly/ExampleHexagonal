package com.example.hexagonal.adapter.`in`.model

import com.example.hexagonal.domain.model.UserRole

data class UserResponseDto(val userName: String?, val role: UserRole?) {
    constructor() : this( null, null)
}