package com.example.hexagonal.adapter.`in`.project

import com.example.hexagonal.adapter.`in`.ProjectControllerAdapter
import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import com.example.hexagonal.adapter.`in`.model.TaskResponseDto
import com.example.hexagonal.port.`in`.ProjectUseCase
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.util.*

class AddTaskToProjectTest {
    private val projectUseCase: ProjectUseCase = mockk()
    private val mockMvc: MockMvc = MockMvcBuilders.standaloneSetup(ProjectControllerAdapter(projectUseCase)).build()

    @Test
    fun `addTaskToProject should add a task to the project and return the updated project`() {
        // Arrange
        val projectId = UUID.randomUUID()
        val taskDescription = "New Task"
        val updatedProject = ProjectResponseDto(projectId, "Test Project", mutableListOf())
        updatedProject.tasks?.add(TaskResponseDto(taskDescription, false, 0))

        every { projectUseCase.addTaskToProject(projectId, taskDescription) } returns updatedProject

        // Act & Assert
        performAddTaskToProjectRequest(projectId, taskDescription)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectId").value(projectId.toString()))
            .andExpect(jsonPath("$.name").value(updatedProject.name))
            .andExpect(jsonPath("$.tasks").isArray)
            .andExpect(jsonPath("$.tasks[0].description").value(taskDescription))
    }

    @Test
    fun `addTaskToProject should handle null project response`() {
        // Arrange
        val projectId = UUID.randomUUID()
        val taskDescription = "New Task"
        every { projectUseCase.addTaskToProject(projectId, taskDescription) } returns null

        // Act & Assert
        performAddTaskToProjectRequest(projectId, taskDescription)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.projectId").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
    }

    private fun performAddTaskToProjectRequest(projectId: UUID, taskDescription: String): ResultActions {
        return mockMvc.perform(
            put("/api/v1/project/{projectId}/add-task", projectId)
                .param("taskDescription", taskDescription)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }
}