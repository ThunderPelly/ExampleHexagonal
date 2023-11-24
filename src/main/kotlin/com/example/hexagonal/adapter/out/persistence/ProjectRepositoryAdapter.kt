package com.example.hexagonal.adapter.out.persistence

import com.example.hexagonal.adapter.out.persistence.model.ProjectEntity
import com.example.hexagonal.port.out.ProjectRepositoryPort
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class ProjectRepositoryAdapter: ProjectRepositoryPort {
    private var projects: MutableMap<UUID, ProjectEntity> = mutableMapOf()

    override fun saveProject(project: ProjectEntity): ProjectEntity {
        projects[project.projectId] = project
        return project
    }

    override fun getProjectById(projectId: UUID): ProjectEntity? {
        return projects[projectId]
    }

    override val allProjects: MutableMap<UUID, ProjectEntity>
        get() = projects
}