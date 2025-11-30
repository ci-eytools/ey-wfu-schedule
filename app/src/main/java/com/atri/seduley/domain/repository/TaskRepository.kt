package com.atri.seduley.domain.repository

import com.atri.seduley.domain.model.Task

interface TaskRepository {

    suspend fun saveTask(task: Task)

    suspend fun getTask(requestCode: Int): Task?

    suspend fun updateTask(task: Task)

    suspend fun clearTask(requestCode: Int)
}