package com.atri.seduley.core.alarm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm

/**
 * 闹钟数据库操作接口
 */
@Dao
interface AlarmDao {

    @Query("SELECT * FROM scheduled_alarm WHERE id = :id")
    suspend fun getScheduledAlarmById(id: Long): ScheduledAlarm?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertScheduledAlarm(alarm: ScheduledAlarm)

    @Query("SELECT * FROM scheduled_alarm")
    fun getAllScheduledAlarms(): List<ScheduledAlarm>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateScheduleAlarm(alarm: ScheduledAlarm)

    @Delete
    suspend fun deleteScheduledAlarm(alarm: ScheduledAlarm)

    @Query("SELECT * FROM message_alarm WHERE id = :id")
    suspend fun getMessageAlarmById(id: Long): MessageAlarm?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessageAlarm(alarm: MessageAlarm)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessageAlarm(alarm: MessageAlarm)

    @Query("SELECT * FROM message_alarm")
    fun getAllMessageAlarms(): List<MessageAlarm>

    @Delete
    suspend fun deleteMessageAlarm(alarm: MessageAlarm)

    @Query("""
        SELECT COUNT(*) FROM (
            SELECT id FROM scheduled_alarm
            UNION ALL
            SELECT id FROM message_alarm
        )
    """)
    suspend fun getAllAlarmsCount(): Int

    @Query("DELETE FROM scheduled_alarm WHERE state IN ('DONE', 'TIME_OUT')")
    suspend fun deleteCompletedScheduledAlarms(): Int

    @Query("DELETE FROM scheduled_alarm WHERE time < :timestamp")
    suspend fun deleteScheduledAlarmsBefore(timestamp: Long): Int

    @Query("DELETE FROM message_alarm WHERE time < :timestamp")
    suspend fun deleteMessageAlarmsBefore(timestamp: Long): Int

    suspend fun deleteAlarmsOlderThan(timestamp: Long): Pair<Int, Int> {
        val scheduledCount = deleteScheduledAlarmsBefore(timestamp)
        val messageCount = deleteMessageAlarmsBefore(timestamp)
        return Pair(scheduledCount, messageCount)
    }

    @Query("DELETE FROM scheduled_alarm")
    suspend fun deleteAllScheduledAlarms(): Int

    @Query("DELETE FROM message_alarm")
    suspend fun deleteAllMessageAlarms(): Int

    @Transaction
    suspend fun deleteAllAlarmsTransaction(): Pair<Int, Int> {
        val scheduledCount = deleteAllScheduledAlarms()
        val messageCount = deleteAllMessageAlarms()
        return Pair(scheduledCount, messageCount)
    }
}
