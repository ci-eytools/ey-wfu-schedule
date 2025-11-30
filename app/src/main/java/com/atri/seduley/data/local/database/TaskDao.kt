package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.atri.seduley.data.local.database.entity.TaskEntity
import com.atri.seduley.data.local.database.entity.TaskState

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query(" SELECT * FROM tasks WHERE state = 0")
    suspend fun getAllAwaitingTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE requestCode = :requestCode")
    suspend fun getTask(requestCode: Int): TaskEntity?

    @Query("DELETE FROM tasks WHERE requestCode = :requestCode")
    suspend fun clear(requestCode: Int)

    @Query("DELETE FROM tasks WHERE state = :state")
    suspend fun clearTaskByState(state: TaskState)
}