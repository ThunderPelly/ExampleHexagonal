package com.example.hexagonal.domain.application

import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.adapter.`in`.model.UserRequestDto
import com.example.hexagonal.adapter.`in`.model.UserResponseDto
import com.example.hexagonal.common.UseCase
import com.example.hexagonal.domain.mapper.toDomain
import com.example.hexagonal.domain.mapper.toEntity
import com.example.hexagonal.domain.mapper.toResponseDto
import com.example.hexagonal.domain.model.User
import com.example.hexagonal.domain.model.UserName
import com.example.hexagonal.domain.model.UserRole
import com.example.hexagonal.port.`in`.UserUseCase
import com.example.hexagonal.port.out.TransactionalPort
import com.example.hexagonal.port.out.UserRepositoryPort
import jakarta.transaction.Transactional

@UseCase
class UserService(private val userRepositoryPort: UserRepositoryPort, private val transactionalPort: TransactionalPort) : UserUseCase {
    private val userRequestDtoExceptionMessage = "UserRequestDto darf nicht null sein."
    private val userNameExceptionMessage = "Der Benutzername darf nicht null oder leer sein."

    private final val usernameMaxLength: Int = 5
    private final val defaultUserRole: UserRole = UserRole.TEAM_MEMBER

    private val userCreationLock = Any() // Ein Sperrobjekt

    /**
     * Erstellt einen neuen Benutzer mit dem angegebenen Benutzernamen, Rolle und speichert ihn im Benutzer-Repository.
     *
     * @param userRequestDto Die Benutzerdaten für die Erstellung des Benutzers.
     * @param role Die Rolle des neuen Benutzers. Standardwert (Teammitglied) wenn leer
     * @return Ein UserResponseDto-Objekt, das den erstellten Benutzer repräsentiert, oder null, wenn die Erstellung fehlschlägt.
     * @throws IllegalArgumentException Wenn die UserRequestDto oder der Benutzername ungültig ist.
     */
    @Transactional
    override fun createUser(userRequestDto: UserRequestDto?): UserResponseDto? {
        // Überprüfen, ob userRequestDto nicht null ist
        requireNotNull(userRequestDto) { userRequestDtoExceptionMessage }

        // Überprüfen, ob der Benutzername nicht null oder leer ist
        require(!userRequestDto.userName.isNullOrBlank()) { userNameExceptionMessage }

        // Setze die Standardrolle, falls die bereitgestellte Rolle null oder leer ist
        val userRole = userRequestDto.role ?: defaultUserRole

        // Überprüfen, ob der Benutzername nicht leer ist
        // Sperren, um sicherzustellen, dass nur ein Thread createUser gleichzeitig ausführt
        synchronized(userCreationLock) {
            // Beginnen einer Transaktion
            transactionalPort.beginTransaction()

            try {
                // Erstellen eines eindeutigen Benutzernamens
                val uniqueUserName = createUniqueUserName(userRequestDto.userName)

                // Benutzer erstellen und im Repository speichern
                val user = User(userName = uniqueUserName, role = userRole)

                userRepositoryPort.saveUser(user.toEntity())

                // Transaktion erfolgreich abschliessen
                transactionalPort.commitTransaction()

                return user.toResponseDto()
            } catch (e: Exception) {
                // Transaktion rückgängig machen und Ausnahme weitergeben
                transactionalPort.rollbackTransaction()
                throw e
            }
        }
    }


    /**
     * Erstellt einen eindeutigen Benutzernamen basierend auf dem gegebenen Benutzernamen.
     * Wenn der Benutzername bereits existiert, wird ein Suffix hinzugefügt, um die Eindeutigkeit sicherzustellen.
     *
     * @param userName Der ursprüngliche Benutzername.
     * @return Ein eindeutiger Benutzername.
     */
    fun createUniqueUserName(userName: String?): UserName {
        var newUserName = UserName(userName)
        var suffix = 0

        // Überprüfe, ob der Benutzername bereits existiert, und füge ein Suffix hinzu, um die Eindeutigkeit sicherzustellen
        while (userRepositoryPort.getUserByUsername(newUserName.value) != null) {
            suffix++
            newUserName = UserName(newUserName.value, suffix)
        }

        return newUserName
    }

    /**
     * Ruft alle Benutzer aus dem Repository ab und gibt sie als Liste von UserResponseDto-Objekten zurück.
     *
     * @return Eine Liste von UserResponseDto-Objekten, die die abgerufenen Benutzer repräsentieren.
     */
    override fun getAllUsers(): List<UserResponseDto>? {
        return userRepositoryPort.allUsers.map { it.toDomain().toResponseDto() }
    }

    /**
     * Ruft die Aufgaben eines bestimmten Benutzers aus dem Repository ab und gibt sie als Liste von TaskResponseDto-Objekten zurück.
     *
     * @param userName Der Benutzername, dessen Aufgaben abgerufen werden sollen.
     * @return Eine Liste von TaskResponseDto-Objekten, die die abgerufenen Aufgaben des Benutzers repräsentieren, oder null, wenn der Benutzer nicht gefunden wird.
     * @throws IllegalArgumentException Wenn der Benutzername ungültig ist.
     */
    override fun getUserTasks(userName: String?): List<TaskResponseDto>? {
        // Überprüfen, ob der Benutzername nicht null oder leer ist
        require(!userName.isNullOrBlank()) { userNameExceptionMessage }

        val user = userRepositoryPort.getUserByUsername(userName)
        return user?.assignedTasks?.map { it.toDomain().toResponseDto() }
    }
}