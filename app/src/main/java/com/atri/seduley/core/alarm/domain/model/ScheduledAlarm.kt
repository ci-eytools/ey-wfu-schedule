package com.atri.seduley.core.alarm.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atri.seduley.core.util.IdUtil
import org.threeten.bp.LocalDateTime

/**
 * 定时任务闹钟（可选是否精确触发）
 */
@Entity(tableName = "scheduled_alarm")
data class ScheduledAlarm(
    @PrimaryKey(autoGenerate = true) override val id: Long = IdUtil.nextId(),
    override val requestCode: Int = id.hashCode(),
    override val title: String,
    override val time: LocalDateTime,
    override val type: AlarmType = AlarmType.SCHEDULED,
    override val state: AlarmState = AlarmState.AWAIT,
    val allowInexact: Boolean = true       // 是否允许误差
) : Alarm(id, requestCode, title, time, type, state) {
    override fun copy(
        id: Long,
        requestCode: Int,
        title: String,
        time: LocalDateTime,
        type: AlarmType,
        state: AlarmState
    ): ScheduledAlarm {
        return ScheduledAlarm(
            id = id,
            requestCode = requestCode,
            title = title,
            time = time,
            type = type,
            state = state,
            allowInexact = this.allowInexact
        )
    }
}
