package com.atri.seduley.feature.course.data.data_score

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.atri.seduley.feature.course.domain.entity.model.Course

@Database(
    entities = [Course::class],
    version = 1
)
abstract class CourseDatabase : RoomDatabase() {

    abstract val courseDao: CourseDao

    companion object {
        const val DATABASE_NAME = "courses.db"

        @Volatile
        private var INSTANCE: CourseDatabase? = null

        fun getDatabase(context: Context): CourseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CourseDatabase::class.java,
                    DATABASE_NAME
                )
//                    .createFromAsset("databases/courses.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}