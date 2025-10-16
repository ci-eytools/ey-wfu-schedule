package com.atri.seduley.core.alarm.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atri.seduley.core.util.IdUtil
import org.threeten.bp.LocalDateTime

/**
 * 消息类闹钟（允许不精确触发）
 */
@Entity(tableName = "message_alarm")
data class MessageAlarm(
    @PrimaryKey(autoGenerate = true) override val id: Long = IdUtil.nextId(),
    override val requestCode: Int = id.hashCode(),
    override val title: String,
    override val time: LocalDateTime,
    override val type: AlarmType = AlarmType.MESSAGE,
    override val state: AlarmState = AlarmState.AWAIT,
    val message: String,
) : Alarm(id, requestCode, title, time, type, state) {
    override fun copy(
        id: Long,
        requestCode: Int,
        title: String,
        time: LocalDateTime,
        type: AlarmType,
        state: AlarmState
    ): MessageAlarm {
        return MessageAlarm(
            id = id,
            requestCode = requestCode,
            title = title,
            time = time,
            type = type,
            state = state,
            message = this.message
        )
    }
}
