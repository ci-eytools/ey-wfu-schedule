package com.atri.seduley.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atri.seduley.data.local.db.converter.Converters
import com.atri.seduley.data.local.db.entity.StudentEntity

@Database(
    entities = [StudentEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class StudentDatabase : RoomDatabase() {

    abstract val studentDao: StudentDao

    companion object {
        const val DATABASE_NAME = "students.db"
    }
}