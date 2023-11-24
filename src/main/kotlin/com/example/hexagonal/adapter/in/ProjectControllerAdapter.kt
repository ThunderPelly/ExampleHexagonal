package com.example.hexagonal.adapter.`in`

import com.example.hexagonal.port.`in`.ProjectUseCase
import com.example.hexagonal.adapter.`in`.model.ProjectRequestDto
import com.example.hexagonal.adapter.`in`.model.ProjectResponseDto
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/v1/project")
class ProjectControllerAdapter(private val projectUseCase: ProjectUseCase) {

    @PostMapping
    fun createProject(@RequestBody projectRequestDto: ProjectRequestDto): ProjectResponseDto? =
        projectUseCase.createProject(projectRequestDto)

    @GetMapping
    fun listProjects(): List<ProjectResponseDto>? =
        projectUseCase.getProjects()

    @PutMapping("/{projectId}/add-task")
    fun addTaskToProject(
        @PathVariable projectId: UUID,
        @RequestParam taskDescription: String
    ): ProjectResponseDto? =
        projectUseCase.addTaskToProject(projectId, taskDescription)
}