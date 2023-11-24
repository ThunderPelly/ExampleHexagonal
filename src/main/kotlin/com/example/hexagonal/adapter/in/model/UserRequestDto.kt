package com.example.hexagonal.adapter.`in`.model

import com.example.hexagonal.domain.model.UserRole
import org.jetbrains.annotations.NotNull

data class UserRequestDto(@NotNull("Der Username darf nicht null oder leer sein") val userName: String?, val role: UserRole?) {
    constructor() : this( null, null)
}