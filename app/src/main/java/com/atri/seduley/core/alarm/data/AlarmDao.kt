package com.atri.seduley.core.alarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("SELECT * FROM scheduled_alarm WHERE id = :id")
    suspend fun getScheduledAlarmById(id: Long): ScheduledAlarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduledAlarm(alarm: ScheduledAlarm)

    @Query("SELECT * FROM scheduled_alarm")
    fun getAllScheduledAlarms(): Flow<List<ScheduledAlarm>>

    @Delete
    suspend fun deleteScheduledAlarm(alarm: ScheduledAlarm)

    @Query("SELECT * FROM message_alarm WHERE id = :id")
    suspend fun getMessageAlarmById(id: Long): MessageAlarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageAlarm(alarm: MessageAlarm)

    @Query("SELECT * FROM message_alarm")
    fun getAllMessageAlarms(): Flow<List<MessageAlarm>>

    @Delete
    suspend fun deleteMessageAlarm(alarm: MessageAlarm)
}
