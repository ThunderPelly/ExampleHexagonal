package com.example.hexagonal.port.out

import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import java.util.*

interface ProjectRepositoryPort {
    fun saveProject(project: ProjectEntity): ProjectEntity
    fun getProjectById(projectId: UUID): ProjectEntity?
    val allProjects: MutableMap<UUID, ProjectEntity>
}