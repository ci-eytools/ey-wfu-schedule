package com.atri.seduley.domain.repository

import com.atri.seduley.data.local.database.entity.TaskState
import com.atri.seduley.domain.model.Task

interface TaskRepository {

    /** 保存任务 */
    suspend fun saveTask(task: Task)

    /** 获取任务 */
    suspend fun getTask(requestCode: Int): Task?

    /** 获取所有等待中的任务 */
    suspend fun getAllAwaitingTasks(): List<Task>

    /** 获取所有任务 */
    suspend fun getAllTasks(): List<Task>

    /** 批量删除任务 */
    suspend fun clearTasks(requestCode: List<Int>)

    /** 更新任务 */
    suspend fun updateTask(task: Task)

    /** 清除任务 */
    suspend fun clearTask(requestCode: Int)

    /** 通过 state 清除任务 */
    suspend fun clearTaskByState(state: TaskState)
}