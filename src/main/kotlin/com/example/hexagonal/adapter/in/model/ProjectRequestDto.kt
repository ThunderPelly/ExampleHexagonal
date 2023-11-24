package com.example.hexagonal.adapter.`in`.model

import com.example.hexagonal.domain.model.UserRole
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class ProjectRequestDto(
    @NotNull(message = "Der Projektname darf nicht null sein")
    @NotBlank(message = "Der Projektname darf nicht null sein")
    val name: String?,
    val role: UserRole?) {
    constructor() : this(null, null)
}

