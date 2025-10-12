package com.atri.seduley.feature.course.data.data_score

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.atri.seduley.feature.course.domain.entity.model.Clazz

@Database(
    entities = [Clazz::class],
    version = 1
)
abstract class ClazzDatabase : RoomDatabase() {

    abstract val clazzDao: ClazzDao

    companion object {
        const val DATABASE_NAME = "clazz.db"

        @Volatile
        private var INSTANCE: ClazzDatabase? = null

        fun getDatabase(context: Context): ClazzDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClazzDatabase::class.java,
                    DATABASE_NAME
                )
//                    .createFromAsset("databases/clazz.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
