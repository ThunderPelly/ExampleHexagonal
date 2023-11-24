package com.example.hexagonal.adapter.out.persistence

import com.example.hexagonal.common.PersistenceAdapter
import com.example.hexagonal.adapter.out.persistence.model.TaskEntity
import com.example.hexagonal.port.out.TaskRepositoryPort


@PersistenceAdapter
class TaskRepositoryAdapter : TaskRepositoryPort {
    private var tasks: MutableList<TaskEntity> = mutableListOf()

    override fun saveTask(task: TaskEntity): TaskEntity {
        tasks.add(task)
        return task
    }

    override val allTasks: List<TaskEntity>
        get() = tasks
}