package com.atri.seduley.data.local.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["requestCode"], unique = true)]
)
data class TaskEntity(
    @PrimaryKey val requestCode: Int,
    val triggerAt: LocalDateTime,
    val triggerMode: TriggerMode,
    val callback: Callback,
    val state: TaskState,
    val params: Map<String, String>
)

/**
 * 在 Receiver 中定义对应的回调方法
 *
 * 例如：UPDATE_COURSE(0)，表示当 callback == UPDATE_COURSE 时触发 updateCourse 方法
 */
enum class Callback(val value: Int) {

}

enum class TriggerMode(val value: Int) {
    INEXACT_ALARM(0),
    EXACT_ALARM(1),
}

enum class TaskState(val value: Int) {
    AWAIT(0),
    DONE(1),
    TIME_OUT(3),
    FAILED(4)
}
