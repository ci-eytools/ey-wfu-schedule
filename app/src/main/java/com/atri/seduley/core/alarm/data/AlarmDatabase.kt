package com.atri.seduley.core.alarm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atri.seduley.core.alarm.domain.model.MessageAlarm
import com.atri.seduley.core.alarm.domain.model.ScheduledAlarm
import com.atri.seduley.core.alarm.util.converter.AlarmEnumConverters
import com.atri.seduley.core.alarm.util.converter.DateTimeConverter

@Database(
    entities = [
        ScheduledAlarm::class,
        MessageAlarm::class
    ],
    version = 1
)
@TypeConverters(DateTimeConverter::class, AlarmEnumConverters::class)
abstract class AlarmDatabase : RoomDatabase() {

    abstract val alarmDao: AlarmDao

    companion object {
        const val DATABASE_NAME = "alarm.db"

        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
