package com.example.hexagonal.domain.application

import com.example.hexagonal.adapter.`in`.model.TaskRequestDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.adapter.out.persistence.model.UserEntity
import com.example.hexagonal.common.UseCase
import com.example.hexagonal.domain.mapper.toDomain
import com.example.hexagonal.domain.mapper.toResponseDto
import com.example.hexagonal.port.`in`.UserTaskUseCase
import com.example.hexagonal.port.out.TaskRepositoryPort
import com.example.hexagonal.port.out.UserRepositoryPort

@UseCase
class UserTaskService(
    private val userRepositoryPort: UserRepositoryPort,
    private val taskRepositoryPort: TaskRepositoryPort
) : UserTaskUseCase {
    private val businessLogicException = "To many low prio Tasks"
    private val userNameExceptionMessage = "Der Benutzername darf nicht null oder leer sein."
    private val taskRequestDtoExceptionMessage = "Benutzername darf nicht null sein."
    private val userOrTaskExceptionMessage = "Benutzer und Aufgabe müssen existieren."
    private val completedTaskExceptionMessage = "Kann keine abgeschlossene Aufgabe einem Benutzer zuweisen."

    /**
     * Diese Methode weist einem Benutzer eine Aufgabe zu, basierend auf bestimmten Bedingungen.
     * Wenn der Benutzer bereits 3 zugewiesene Aufgaben hat oder wenn eine seiner zugewiesenen Aufgaben
     * eine Priorität grösser als 3 hat, wird die Aufgabe neu zugewiesen.
     * Andernfalls wird die Aufgabe dem Benutzer zugewiesen.
     *
     * @param userName Der Benutzername, dem die Aufgabe zugewiesen werden soll.
     * @param taskRequestDto Die Beschreibung der Aufgabe, die zugewiesen werden soll.
     * @return Ein TaskResponseDto-Objekt, das die zugewiesene Aufgabe repräsentiert, oder null, wenn keine Aufgabe zugewiesen wurde.
     * @throws IllegalArgumentException Wenn TaskRequestDto oder Benutzer oder Aufgabe nicht existieren.
     */
    override fun assignTaskToUser(userName: String?, taskRequestDto: TaskRequestDto?): TaskResponseDto? {
        // Überprüfen, ob taskRequestDto nicht null ist
        requireNotNull(taskRequestDto) { taskRequestDtoExceptionMessage }
        // Überprüfen, ob das TaskRequestDto nicht null oder leer ist
        require(!userName.isNullOrBlank()) { userNameExceptionMessage }

        val user = userRepositoryPort.getUserByUsername(userName)
        val task = taskRepositoryPort.allTasks.find { it.description == taskRequestDto.description }

        require(!(user == null || task == null)) { userOrTaskExceptionMessage }

        // Simulierte Geschäftslogik: Je nach bestimmten Bedingungen Aufgaben zuweisen oder neu zuweisen
        when {
            shouldAssignTask(user) -> assignTask(user, task)
            shouldReassignTask(user) -> reassignTask(user, task)
            else -> throw IllegalStateException(businessLogicException)
        }

        return task.toDomain().toResponseDto()
    }

    /**
     * Determines whether a task should be assigned to the user based on a business rule.
     *
     * @param user The user to check for task assignment eligibility.
     * @return True if a task should be assigned, false otherwise.
     */
    private fun shouldAssignTask(user: UserEntity): Boolean {
        return user.assignedTasks.size  < 3
    }

    /**
     * Determines whether a task should be reassigned to the user based on a business rule.
     *
     * @param user The user to check for task reassignment eligibility.
     * @return True if a task should be reassigned, false otherwise.
     */
    private fun shouldReassignTask(user: UserEntity): Boolean {
        return user.assignedTasks.any { it.priority > 3 }
    }

    /**
     * Weist eine Aufgabe einem Benutzer zu und speichert die Änderungen im Task-Repository.
     *
     * @param user Der Benutzer, dem die Aufgabe zugewiesen wird.
     * @param task Die Aufgabe, die zugewiesen wird.
     * @throws IllegalArgumentException Wenn versucht wird, eine abgeschlossene Aufgabe einem Benutzer zuzuweisen.
     */
    private fun assignTask(user: UserEntity, task: TaskEntity) {
        check(!task.isCompleted) { completedTaskExceptionMessage }

        user.assignedTasks.add(task)
        taskRepositoryPort.saveTask(task)
    }

    /**
     * Weist eine Aufgabe einem Benutzer zu. Der neue Task erhält die höchste Priorität aller Tasks.
     *
     * @param user Der Benutzer, dem die Aufgabe neu zugewiesen wird.
     * @param task Die Aufgabe, die neu zugewiesen wird.
     */
    private fun reassignTask(user: UserEntity, task: TaskEntity) {
        val highestPriority = user.assignedTasks.maxByOrNull { it.priority }!!.priority
        task.priority = highestPriority + 1
        user.assignedTasks.add(task)

        taskRepositoryPort.saveTask(task)
    }
}