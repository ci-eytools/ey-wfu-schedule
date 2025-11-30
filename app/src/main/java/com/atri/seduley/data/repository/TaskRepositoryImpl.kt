package com.atri.seduley.data.repository

import com.atri.seduley.data.local.database.TaskDao
import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.domain.model.Task
import com.atri.seduley.domain.model.mapper.toDomain
import com.atri.seduley.domain.model.mapper.toEntity
import com.atri.seduley.domain.repository.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    /** 保存任务 */
    override suspend fun saveTask(task: Task) {
        taskDao.insert(task.toEntity())
    }

    /** 获取任务 */
    override suspend fun getTask(requestCode: Int): Task? {
        return taskDao.getTask(requestCode)?.toDomain()
    }

    /** 获取所有等待中的任务 */
    override suspend fun getAllAwaitingTasks(): List<Task> {
        return taskDao.getAllAwaitingTasks().map { it.toDomain() }
    }

    /** 更新任务 */
    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    /** 清除任务 */
    override suspend fun clearTask(requestCode: Int) {
        taskDao.clear(requestCode)
    }

    /** 通过 state 清除任务 */
    override suspend fun clearTaskByState(state: TaskState) {
        taskDao.clearTaskByState(state)
    }
}