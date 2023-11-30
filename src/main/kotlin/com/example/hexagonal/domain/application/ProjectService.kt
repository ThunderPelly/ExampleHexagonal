package com.example.hexagonal.domain.application

import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import com.example.hexagonal.domain.application.exceptions.InsufficientPermissionException
import com.example.hexagonal.domain.mapper.toDomain
import com.example.hexagonal.domain.mapper.toEntity
import com.example.hexagonal.domain.mapper.toResponseDto
import com.example.hexagonal.domain.model.Project
import com.example.hexagonal.domain.model.Task
import com.example.hexagonal.domain.model.TaskDescription
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.`in`.ProjectUseCase
import com.example.hexagonal.port.out.ProjectRepositoryPort
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProjectService(private val projectRepositoryPort: ProjectRepositoryPort) : ProjectUseCase {
    private val projectRequestDtoExceptionMessage = "ProjectRequestDto darf nicht null oder leer sein."
    private val projectExceptionMessage = "Die Projekt-Id darf nicht null oder leer sein."
    private val descriptionExceptionMessage = "Die Aufgabenbeschreibung  darf nicht null oder leer sein."
    private val projectNameExceptionMessage = "Der Projektname darf nicht null oder leer sein."
    private val insufficientPermissionExceptionMessage =
        "Benutzer hat nicht die erforderliche Rolle, um ein Projekt zu erstellen."
    /**
     * Erstellt ein neues Projekt mit dem angegebenen Projektname und speichert es im Repository.
     *
     * @param projectName Der Name des neuen Projekts.
     * @return Ein ProjectResponseDto-Objekt, das das erstellte Projekt repräsentiert.
     * @throws IllegalArgumentException Wenn der Projektname ungültig ist.
     * @throws IllegalArgumentException Wenn der Benutzer nicht die erforderliche Rolle hat, um ein Projekt zu erstellen.
     */
    override fun createProject(projectRequestDto: ProjectRequestDto?): ProjectResponseDto {
        if (projectRequestDto != null) {
            // Überprüfen, ob der Benutzer die erforderliche Rolle hat, um ein Projekt zu erstellen
            if(projectRequestDto.role != UserRole.MANAGER) throw InsufficientPermissionException(insufficientPermissionExceptionMessage)
            // Überprüfen, ob der Projektname nicht null oder leer ist
            require(!projectRequestDto.name.isNullOrBlank()) { projectNameExceptionMessage }
            val project = Project(name = projectRequestDto.name)
            projectRepositoryPort.saveProject(project.toEntity())
            return project.toResponseDto()
        } else {
            throw IllegalArgumentException(projectRequestDtoExceptionMessage)
        }
    }

    /**
     * Ruft alle Projekte aus dem Repository ab und gibt sie als Liste von ProjectResponseDto-Objekten zurück.
     *
     * @return Eine Liste von ProjectResponseDto-Objekten, die die abgerufenen Projekte repräsentieren.
     */
    override fun getProjects(): List<ProjectResponseDto>? {
        val projects = projectRepositoryPort.allProjects.map { (projectId, project) ->
            ProjectResponseDto(
                projectId = projectId,
                name = project.name,
                tasks = project.tasks.map { it.toDomain().toResponseDto() }.toMutableList()
            )
        }
        return projects
    }

    /**
     * Fügt eine Aufgabe zu einem Projekt hinzu und gibt das aktualisierte Projekt als ProjectResponseDto-Objekt zurück.
     *
     * @param projectId Die UUID des Projekts, zu dem die Aufgabe hinzugefügt werden soll.
     * @param taskDescription Die Beschreibung der hinzuzufügenden Aufgabe.
     * @return Ein ProjectResponseDto-Objekt, das das aktualisierte Projekt repräsentiert, oder null, wenn das Projekt nicht gefunden wird.
     * @throws IllegalArgumentException Wenn die Projekt-Id oder die Aufgabenbeschreibung ungültig ist.
     */
    override fun addTaskToProject(projectId: UUID?, taskDescription: String?): ProjectResponseDto? {
        // Überprüfen, ob der Projekt-Id nicht null oder leer ist
        require(projectId.isValidUuid()) { projectExceptionMessage }
        val project = projectId?.let { projectRepositoryPort.getProjectById(it) }
        val task = Task(description = TaskDescription(taskDescription))
        project?.let {
            it.tasks.add(task.toEntity())
            projectRepositoryPort.saveProject(project)
        }
        return project?.toDomain()?.toResponseDto()
    }

    fun UUID?.isValidUuid(): Boolean {
        return this != null && this.toString().isNotBlank()
    }
}

