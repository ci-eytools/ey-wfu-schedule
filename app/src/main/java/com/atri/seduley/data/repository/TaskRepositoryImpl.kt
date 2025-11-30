package com.atri.seduley.data.repository

import com.atri.seduley.data.local.database.TaskDao
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun saveTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    override suspend fun getTask(requestCode: Int): Task? {
        return taskDao.getTask(requestCode)?.toDomain()
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun clearTask(requestCode: Int) {
        taskDao.clear(requestCode)
    }
}