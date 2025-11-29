package com.atri.seduley.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atri.seduley.data.local.database.converter.Converters
import com.atri.seduley.data.local.database.entity.CourseEntity
import com.atri.seduley.data.local.database.entity.StudentEntity

@Database(
    entities = [StudentEntity::class, CourseEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SeduleyDatabase : RoomDatabase() {

    abstract val studentDao: StudentDao
    abstract val courseDao: CourseDao

    companion object {
        const val DATABASE_NAME = "seduley.db"
    }
}