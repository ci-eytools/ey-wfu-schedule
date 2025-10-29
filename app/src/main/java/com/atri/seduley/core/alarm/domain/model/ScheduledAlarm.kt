package com.atri.seduley.core.alarm.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atri.seduley.core.util.IdUtil
import org.threeten.bp.LocalDateTime

/**
 * 定时任务闹钟
 */
@Entity(
    tableName = "scheduled_alarm",
    indices = [Index(value = ["title", "time", "type", "triggerMode"], unique = true)]
)
data class ScheduledAlarm(
    @PrimaryKey(autoGenerate = false) override val id: Long = IdUtil.nextId(),
    override val requestCode: Int = id.hashCode(),
    override val title: String,
    override val time: LocalDateTime,
    override val type: AlarmType = AlarmType.SCHEDULED,
    override val triggerMode: TriggerMode,
    override val state: AlarmState = AlarmState.AWAIT,
    val scheduledProperty: String? = null
) : Alarm(id, requestCode, title, time, type, triggerMode, state) {
    override fun copy(
        id: Long,
        requestCode: Int,
        title: String,
        time: LocalDateTime,
        type: AlarmType,
        triggerMode: TriggerMode,
        state: AlarmState
    ): ScheduledAlarm {
        return ScheduledAlarm(
            id = id,
            requestCode = requestCode,
            title = title,
            time = time,
            type = type,
            triggerMode = triggerMode,
            state = state,
            scheduledProperty = this.scheduledProperty
        )
    }
}
