package com.atri.seduley.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.data.local.database.entity.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE requestCode = :requestCode")
    suspend fun getTask(requestCode: Int): TaskEntity?
}