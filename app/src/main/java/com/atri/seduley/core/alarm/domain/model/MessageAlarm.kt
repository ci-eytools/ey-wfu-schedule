package com.atri.seduley.core.alarm.domain.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.atri.seduley.core.util.IdUtil
import org.threeten.bp.LocalDateTime

/**
 * 消息类闹钟
 */
@Entity(
    tableName = "message_alarm",
    indices = [Index(value = ["title", "time", "type", "triggerMode"], unique = true)]
)
data class MessageAlarm(
    @PrimaryKey(autoGenerate = false) override val id: Long = IdUtil.nextId(),
    override val requestCode: Int = id.hashCode(),
    override val title: String,
    override val time: LocalDateTime,
    override val type: AlarmType = AlarmType.MESSAGE,
    override val triggerMode: TriggerMode,
    override val state: AlarmState = AlarmState.AWAIT,
    val message: String,
) : Alarm(id, requestCode, title, time, type, triggerMode, state) {
    override fun copy(
        id: Long,
        requestCode: Int,
        title: String,
        time: LocalDateTime,
        type: AlarmType,
        triggerMode: TriggerMode,
        state: AlarmState
    ): MessageAlarm {
        return MessageAlarm(
            id = id,
            requestCode = requestCode,
            title = title,
            time = time,
            type = type,
            triggerMode = triggerMode,
            state = state,
            message = this.message
        )
    }
}
