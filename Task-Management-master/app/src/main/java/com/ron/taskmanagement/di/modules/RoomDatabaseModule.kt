package com.ron.taskmanagement.di.modules

import android.content.Context
import androidx.room.Room
import com.ron.taskmanagement.di.room.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(ViewModelComponent::class)
@Module
class RoomDatabaseModule {

    @Provides
    fun provideDatabaseInstance(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "ron_task.db"
        ).build()
    }

    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao


//    @Singleton
//    @Provides
//    fun provideTaskRepo() = TaskRepository()

}