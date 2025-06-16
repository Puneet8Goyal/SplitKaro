package com.ron.taskmanagement.di.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ron.taskmanagement.models.Task
import com.ron.taskmanagement.di.room.converters.TypeConverter
import com.ron.taskmanagement.di.room.dao.TaskDao

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract val taskDao : TaskDao

//    companion object {
//        @Volatile
//        private var INSTANCE: TaskDatabase? = null
//        fun getInstance(context: Context): TaskDatabase {
//            synchronized(this) {
//                return INSTANCE ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    TaskDatabase::class.java,
//                    "task_db"
//                ).build().also {
//                    INSTANCE = it
//                }
//            }
//
//        }
//    }

}