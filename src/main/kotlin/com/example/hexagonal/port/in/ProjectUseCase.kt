package com.example.hexagonal.port.`in`

import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import java.util.*

interface ProjectUseCase {
    fun createProject(projectRequestDto: ProjectRequestDto?): ProjectResponseDto?

    fun getProjects(): List<ProjectResponseDto>?

    fun addTaskToProject(projectId: UUID?, taskDescription: String?): ProjectResponseDto?
}