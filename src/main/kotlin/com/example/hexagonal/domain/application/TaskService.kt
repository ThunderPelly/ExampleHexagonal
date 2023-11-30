package com.example.hexagonal.domain.application

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.common.UseCase
import com.example.hexagonal.domain.mapper.toDomain
import com.example.hexagonal.domain.mapper.toEntity
import com.example.hexagonal.domain.mapper.toResponseDto
import com.example.hexagonal.domain.model.Task
import com.example.hexagonal.domain.model.TaskDescription
import com.example.hexagonal.port.`in`.TaskUseCase
import com.example.hexagonal.port.out.TaskRepositoryPort

@UseCase
class TaskService(private val taskRepositoryPort: TaskRepositoryPort) : TaskUseCase {
    private val taskRequestDtoExceptionMessage = "TaskRequestDto darf nicht null sein."
    private val descriptionExceptionMessage = "Die Beschreibung darf nicht null oder leer sein."
    private val priorityExceptionMessage = "Die Priorität darf nicht null oder leer sein."

    /**
     * Erstellt eine neue Aufgabe basierend auf den bereitgestellten Aufgabendaten und speichert sie im Repository.
     *
     * @param taskRequestDto Die Aufgabendaten für die Erstellung der Aufgabe.
     * @return Ein TaskResponseDto-Objekt, das die erstellte Aufgabe repräsentiert.
     * @throws IllegalArgumentException Wenn die TaskRequestDto oder Task-Beschreibung ungültig ist.
     */
    override fun createTask(taskRequestDto: TaskRequestDto?): TaskResponseDto? {
        // Überprüfen, ob taskRequestDto nicht null ist
        requireNotNull(taskRequestDto) { taskRequestDtoExceptionMessage }

        val task = Task(description = TaskDescription(taskRequestDto.description))
        taskRepositoryPort.saveTask(task.toEntity())
        return task.toResponseDto()
    }

    /**
     * Ruft alle Aufgaben aus dem Repository ab und gibt sie als Liste von TaskResponseDto-Objekten zurück.
     *
     * @return Eine Liste von TaskResponseDto-Objekten, die die abgerufenen Aufgaben repräsentieren.
     */
    override fun getAllTasks(): List<TaskResponseDto>? {
        return taskRepositoryPort.allTasks.map { it.toDomain().toResponseDto() }
    }

    /**
     * Markiert eine Aufgabe als abgeschlossen und speichert die Änderung im Repository.
     *
     * @param description Die Beschreibung der abzuschließenden Aufgabe.
     * @return Ein TaskResponseDto-Objekt, das die aktualisierte Aufgabe repräsentiert, oder null, wenn die Aufgabe nicht gefunden wird.
     * @throws IllegalArgumentException Wenn die Task-Beschreibung ungültig ist.
     */
    override fun completeTask(description: String?): TaskResponseDto? {
        require(!description.isNullOrBlank()) { descriptionExceptionMessage }

        val task = taskRepositoryPort.allTasks.find { it.description == description }
        task?.let {
            it.isCompleted = true
            taskRepositoryPort.saveTask(it)
        }
        return task?.toDomain()?.toResponseDto()
    }

    /**
     * Setzt die Priorität einer Aufgabe und speichert die Änderung im Repository.
     *
     * @param description Die Beschreibung der Aufgabe, deren Priorität geändert werden soll.
     * @param priority Die neue Priorität für die Aufgabe.
     * @return Ein TaskResponseDto-Objekt, das die aktualisierte Aufgabe repräsentiert, oder null, wenn die Aufgabe nicht gefunden wird.
     * @throws IllegalArgumentException Wenn die Task-Beschreibung oder die Priorität ungültig ist.
     */
    override fun setTaskPriority(description: String?, priority: Int?): TaskResponseDto? {
        // Überprüfen, ob der Beschreibung und die Priorität nicht null oder leer ist
        require(!description.isNullOrBlank()) { descriptionExceptionMessage }
        require(priority != null && priority > 0) { priorityExceptionMessage }
        val task = taskRepositoryPort.allTasks.find { it.description == description }
        task?.let {
            it.priority = priority
            taskRepositoryPort.saveTask(it)
        }
        return task?.toDomain()?.toResponseDto()
    }
}

